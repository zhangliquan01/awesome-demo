package com.nlp.service;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.nlp.model.NlpRequest;
import com.nlp.model.NlpResponse;
import com.vdurmont.emoji.EmojiParser;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * NLP服务实现类
 */
@Service
public class NlpService {

    private final StanfordCoreNLP stanfordCoreNlpEnglish;
    @SuppressWarnings("unused") // 保留以备将来扩展使用
    private final StanfordCoreNLP stanfordCoreNlpChinese;
    private final JLanguageTool languageToolEnglish;
    private final JLanguageTool languageToolChinese;

    // 常见英文缩写映射
    private static final Map<String, String> ABBREVIATIONS = new HashMap<>();
    
    static {
        ABBREVIATIONS.put("don't", "do not");
        ABBREVIATIONS.put("can't", "cannot");
        ABBREVIATIONS.put("won't", "will not");
        ABBREVIATIONS.put("shouldn't", "should not");
        ABBREVIATIONS.put("wouldn't", "would not");
        ABBREVIATIONS.put("couldn't", "could not");
        ABBREVIATIONS.put("i'm", "i am");
        ABBREVIATIONS.put("you're", "you are");
        ABBREVIATIONS.put("we're", "we are");
        ABBREVIATIONS.put("they're", "they are");
        ABBREVIATIONS.put("it's", "it is");
        ABBREVIATIONS.put("that's", "that is");
        ABBREVIATIONS.put("i've", "i have");
        ABBREVIATIONS.put("you've", "you have");
        ABBREVIATIONS.put("we've", "we have");
        ABBREVIATIONS.put("they've", "they have");
        ABBREVIATIONS.put("i'll", "i will");
        ABBREVIATIONS.put("you'll", "you will");
        ABBREVIATIONS.put("he'll", "he will");
        ABBREVIATIONS.put("she'll", "she will");
        ABBREVIATIONS.put("we'll", "we will");
        ABBREVIATIONS.put("they'll", "they will");
        ABBREVIATIONS.put("isn't", "is not");
        ABBREVIATIONS.put("aren't", "are not");
        ABBREVIATIONS.put("wasn't", "was not");
        ABBREVIATIONS.put("weren't", "were not");
        ABBREVIATIONS.put("hasn't", "has not");
        ABBREVIATIONS.put("haven't", "have not");
        ABBREVIATIONS.put("hadn't", "had not");
        ABBREVIATIONS.put("doesn't", "does not");
        ABBREVIATIONS.put("didn't", "did not");
        ABBREVIATIONS.put("u", "you");
        ABBREVIATIONS.put("ur", "your");
        ABBREVIATIONS.put("thx", "thanks");
        ABBREVIATIONS.put("plz", "please");
        ABBREVIATIONS.put("pls", "please");
        ABBREVIATIONS.put("btw", "by the way");
        ABBREVIATIONS.put("omg", "oh my god");
        ABBREVIATIONS.put("lol", "laugh out loud");
        ABBREVIATIONS.put("asap", "as soon as possible");
    }

    public NlpService(
            @Qualifier("stanfordCoreNlpEnglish") StanfordCoreNLP stanfordCoreNlpEnglish,
            @Qualifier("stanfordCoreNlpChinese") StanfordCoreNLP stanfordCoreNlpChinese,
            @Qualifier("languageToolEnglish") JLanguageTool languageToolEnglish,
            @Qualifier("languageToolChinese") JLanguageTool languageToolChinese) {
        this.stanfordCoreNlpEnglish = stanfordCoreNlpEnglish;
        this.stanfordCoreNlpChinese = stanfordCoreNlpChinese;
        this.languageToolEnglish = languageToolEnglish;
        this.languageToolChinese = languageToolChinese;
    }

    /**
     * 处理NLP请求
     */
    public NlpResponse process(NlpRequest request) {
        long startTime = System.currentTimeMillis();
        
        String text = request.getKeyword();
        String language = detectLanguage(text, request.getLanguage());
        
        NlpResponse.NlpResponseBuilder builder = NlpResponse.builder()
                .originalText(text)
                .detectedLanguage(language);

        // 分词
        if (shouldEnable(request.getEnableAll(), request.getEnableTokenization())) {
            List<String> tokens = tokenize(text, language);
            builder.tokens(tokens);
            
            List<NlpResponse.TokenInfo> tokenDetails = getTokenDetails(text, language);
            builder.tokenDetails(tokenDetails);
        }

        // 纠错
        if (shouldEnable(request.getEnableAll(), request.getEnableSpellCheck())) {
            NlpResponse.SpellCheckResult spellCheck = checkSpelling(text, language);
            builder.spellCheck(spellCheck);
        }

        // 大小写归一化
        if (shouldEnable(request.getEnableAll(), request.getEnableNormalization())) {
            String normalized = normalizeCase(text);
            builder.normalizedText(normalized);
        }

        // 缩写还原
        if (shouldEnable(request.getEnableAll(), request.getEnableAbbreviationExpansion())) {
            String expanded = expandAbbreviations(text);
            builder.expandedText(expanded);
        }

        // 表情符号处理
        if (shouldEnable(request.getEnableAll(), request.getEnableEmojiProcessing())) {
            NlpResponse.EmojiResult emojiResult = processEmojis(text);
            builder.emojiResult(emojiResult);
        }

        // 命名实体识别
        if (shouldEnable(request.getEnableAll(), request.getEnableNer())) {
            List<NlpResponse.NamedEntity> entities = recognizeNamedEntities(text, language);
            builder.namedEntities(entities);
        }

        long endTime = System.currentTimeMillis();
        builder.processingTime(endTime - startTime);

        return builder.build();
    }

    /**
     * 判断是否启用某个功能
     */
    private boolean shouldEnable(Boolean enableAll, Boolean specificEnable) {
        if (enableAll != null && enableAll) {
            return true;
        }
        return specificEnable != null && specificEnable;
    }

    /**
     * 检测语言
     */
    private String detectLanguage(String text, String requestLanguage) {
        if (!"auto".equalsIgnoreCase(requestLanguage)) {
            return requestLanguage;
        }
        
        // 简单的语言检测：检查是否包含中文字符
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = chinesePattern.matcher(text);
        
        if (matcher.find()) {
            return "zh";
        }
        return "en";
    }

    /**
     * 分词
     */
    private List<String> tokenize(String text, String language) {
        if ("zh".equalsIgnoreCase(language)) {
            // 使用HanLP进行中文分词
            List<Term> terms = HanLP.segment(text);
            return terms.stream()
                    .map(term -> term.word)
                    .collect(Collectors.toList());
        } else {
            // 使用Stanford CoreNLP进行英文分词
            CoreDocument document = new CoreDocument(text);
            stanfordCoreNlpEnglish.annotate(document);
            
            return document.tokens().stream()
                    .map(CoreLabel::word)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取词性标注详情
     */
    private List<NlpResponse.TokenInfo> getTokenDetails(String text, String language) {
        List<NlpResponse.TokenInfo> tokenInfos = new ArrayList<>();
        
        if ("zh".equalsIgnoreCase(language)) {
            // 中文词性标注
            List<Term> terms = HanLP.segment(text);
            for (Term term : terms) {
                tokenInfos.add(NlpResponse.TokenInfo.builder()
                        .word(term.word)
                        .pos(term.nature.toString())
                        .ner("")
                        .build());
            }
        } else {
            // 英文词性标注和命名实体识别
            CoreDocument document = new CoreDocument(text);
            stanfordCoreNlpEnglish.annotate(document);
            
            for (CoreLabel token : document.tokens()) {
                tokenInfos.add(NlpResponse.TokenInfo.builder()
                        .word(token.word())
                        .pos(token.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                        .ner(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
                        .build());
            }
        }
        
        return tokenInfos;
    }

    /**
     * 拼写检查
     */
    private NlpResponse.SpellCheckResult checkSpelling(String text, String language) {
        try {
            JLanguageTool langTool = "zh".equalsIgnoreCase(language) 
                    ? languageToolChinese 
                    : languageToolEnglish;
            
            List<RuleMatch> matches = langTool.check(text);
            List<NlpResponse.SpellError> errors = new ArrayList<>();
            String correctedText = text;
            
            // 从后往前替换，避免位置偏移
            List<RuleMatch> reversedMatches = new ArrayList<>(matches);
            Collections.reverse(reversedMatches);
            
            for (RuleMatch match : reversedMatches) {
                List<String> suggestions = match.getSuggestedReplacements();
                
                errors.add(NlpResponse.SpellError.builder()
                        .original(text.substring(match.getFromPos(), match.getToPos()))
                        .suggestions(suggestions.isEmpty() ? Collections.emptyList() : 
                                suggestions.subList(0, Math.min(3, suggestions.size())))
                        .message(match.getMessage())
                        .position(match.getFromPos())
                        .build());
                
                // 如果有建议，使用第一个建议进行纠正
                if (!suggestions.isEmpty()) {
                    correctedText = correctedText.substring(0, match.getFromPos()) 
                            + suggestions.get(0) 
                            + correctedText.substring(match.getToPos());
                }
            }
            
            Collections.reverse(errors); // 恢复原始顺序
            
            return NlpResponse.SpellCheckResult.builder()
                    .correctedText(correctedText)
                    .errors(errors)
                    .hasErrors(!errors.isEmpty())
                    .build();
                    
        } catch (Exception e) {
            return NlpResponse.SpellCheckResult.builder()
                    .correctedText(text)
                    .errors(Collections.emptyList())
                    .hasErrors(false)
                    .build();
        }
    }

    /**
     * 大小写归一化
     */
    private String normalizeCase(String text) {
        // 转换为小写
        return text.toLowerCase();
    }

    /**
     * 缩写还原
     */
    private String expandAbbreviations(String text) {
        String result = text;
        
        // 使用正则表达式匹配单词边界，避免部分匹配
        for (Map.Entry<String, String> entry : ABBREVIATIONS.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        
        return result;
    }

    /**
     * 表情符号处理
     */
    private NlpResponse.EmojiResult processEmojis(String text) {
        // 提取所有表情符号
        List<NlpResponse.EmojiInfo> emojiInfos = new ArrayList<>();
        
        // 使用emoji-java库解析表情符号
        List<com.vdurmont.emoji.Emoji> emojis = EmojiParser.extractEmojis(text).stream()
                .map(emojiStr -> com.vdurmont.emoji.EmojiManager.getByUnicode(emojiStr))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        for (com.vdurmont.emoji.Emoji emoji : emojis) {
            emojiInfos.add(NlpResponse.EmojiInfo.builder()
                    .emoji(emoji.getUnicode())
                    .description(emoji.getDescription())
                    .unicode(emoji.getUnicode())
                    .build());
        }
        
        // 移除表情符号
        String textWithoutEmojis = EmojiParser.removeAllEmojis(text);
        
        return NlpResponse.EmojiResult.builder()
                .textWithoutEmojis(textWithoutEmojis)
                .emojis(emojiInfos)
                .emojiCount(emojiInfos.size())
                .build();
    }

    /**
     * 命名实体识别
     */
    private List<NlpResponse.NamedEntity> recognizeNamedEntities(String text, String language) {
        List<NlpResponse.NamedEntity> entities = new ArrayList<>();
        
        if ("zh".equalsIgnoreCase(language)) {
            // 使用HanLP进行中文命名实体识别
            extractChineseEntitiesUsingHanLP(text, entities);
        } else {
            // 使用Stanford CoreNLP英文NER
            CoreDocument document = new CoreDocument(text);
            stanfordCoreNlpEnglish.annotate(document);
            
            extractEntitiesFromDocument(document, entities);
        }
        
        return entities;
    }
    
    /**
     * 使用HanLP提取中文命名实体
     */
    private void extractChineseEntitiesUsingHanLP(String text, List<NlpResponse.NamedEntity> entities) {
        List<Term> terms = HanLP.segment(text);
        
        int position = 0;
        for (Term term : terms) {
            String nature = term.nature.toString();
            String entityType = null;
            
            // 根据HanLP的词性标注判断实体类型
            if (nature.startsWith("nr")) {
                // nr: 人名
                entityType = "PERSON";
            } else if (nature.startsWith("ns")) {
                // ns: 地名
                entityType = "LOCATION";
            } else if (nature.startsWith("nt")) {
                // nt: 机构团体名
                entityType = "ORGANIZATION";
            } else if (nature.startsWith("nz")) {
                // nz: 其他专名（可能是机构或品牌）
                entityType = "ORGANIZATION";
            }
            
            if (entityType != null) {
                entities.add(NlpResponse.NamedEntity.builder()
                        .text(term.word)
                        .type(entityType)
                        .startPosition(position)
                        .endPosition(position + term.word.length())
                        .build());
            }
            
            position += term.word.length();
        }
    }

    /**
     * 从CoreDocument中提取命名实体
     */
    private void extractEntitiesFromDocument(CoreDocument document, List<NlpResponse.NamedEntity> entities) {
        List<CoreLabel> tokens = document.tokens();
        
        int i = 0;
        while (i < tokens.size()) {
            CoreLabel token = tokens.get(i);
            String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            
            if (ner != null && !"O".equals(ner)) {
                StringBuilder entityText = new StringBuilder(token.word());
                int startPos = token.beginPosition();
                int endPos = token.endPosition();
                
                // 合并连续的相同类型实体
                int j = i + 1;
                while (j < tokens.size()) {
                    CoreLabel nextToken = tokens.get(j);
                    String nextNer = nextToken.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    
                    if (ner.equals(nextNer)) {
                        entityText.append(" ").append(nextToken.word());
                        endPos = nextToken.endPosition();
                        j++;
                    } else {
                        break;
                    }
                }
                
                entities.add(NlpResponse.NamedEntity.builder()
                        .text(entityText.toString())
                        .type(ner)
                        .startPosition(startPos)
                        .endPosition(endPos)
                        .build());
                
                i = j;
            } else {
                i++;
            }
        }
    }
}


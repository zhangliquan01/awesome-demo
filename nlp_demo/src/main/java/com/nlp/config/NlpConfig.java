package com.nlp.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.Chinese;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * NLP配置类
 */
@Configuration
public class NlpConfig {

    /**
     * Stanford CoreNLP - 英文处理
     */
    @Bean(name = "stanfordCoreNlpEnglish")
    public StanfordCoreNLP stanfordCoreNlpEnglish() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("tokenize.language", "en");
        props.setProperty("ner.useSUTime", "false");
        props.setProperty("threads", "4");
        return new StanfordCoreNLP(props);
    }

    /**
     * Stanford CoreNLP - 中文处理
     * 注意：中文只使用tokenize和pos，NER使用HanLP实现
     */
    @Bean(name = "stanfordCoreNlpChinese")
    public StanfordCoreNLP stanfordCoreNlpChinese() {
        Properties props = new Properties();
        // 中文只使用分词和词性标注，不使用lemma和ner
        props.setProperty("annotators", "tokenize,ssplit,pos");
        props.setProperty("tokenize.language", "zh");
        props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
        props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese");
        props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
        props.setProperty("segment.sighanPostProcessing", "true");
        return new StanfordCoreNLP(props);
    }

    /**
     * LanguageTool - 英文拼写检查
     */
    @Bean(name = "languageToolEnglish")
    public JLanguageTool languageToolEnglish() {
        return new JLanguageTool(new AmericanEnglish());
    }

    /**
     * LanguageTool - 中文检查
     */
    @Bean(name = "languageToolChinese")
    public JLanguageTool languageToolChinese() {
        return new JLanguageTool(new Chinese());
    }
}


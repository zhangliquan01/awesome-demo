#!/bin/bash

echo "=========================================="
echo "  NLP工具API测试脚本"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8080/api/nlp"

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试健康检查
echo -e "${YELLOW}1. 测试健康检查接口${NC}"
echo "GET $BASE_URL/health"
curl -s -X GET "$BASE_URL/health" | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试英文分词
echo -e "${YELLOW}2. 测试英文分词${NC}"
echo "POST $BASE_URL/tokenize"
curl -s -X POST "$BASE_URL/tokenize" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Natural Language Processing is amazing!",
    "language": "en"
  }' | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试中文分词
echo -e "${YELLOW}3. 测试中文分词${NC}"
echo "POST $BASE_URL/tokenize"
curl -s -X POST "$BASE_URL/tokenize" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "今天天气真不错",
    "language": "zh"
  }' | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试拼写检查
echo -e "${YELLOW}4. 测试拼写检查${NC}"
echo "POST $BASE_URL/spell-check"
curl -s -X POST "$BASE_URL/spell-check" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "I have a speling mistake here",
    "language": "en"
  }' | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试命名实体识别
echo -e "${YELLOW}5. 测试命名实体识别${NC}"
echo "POST $BASE_URL/ner"
curl -s -X POST "$BASE_URL/ner" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Apple Inc. is located in California.",
    "language": "en"
  }' | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试表情符号处理
echo -e "${YELLOW}6. 测试表情符号处理${NC}"
echo "POST $BASE_URL/emoji"
curl -s -X POST "$BASE_URL/emoji" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Hello! 😊 How are you? 👋"
  }' | json_pp || echo ""
echo ""
echo "---"
echo ""

# 测试完整处理
echo -e "${YELLOW}7. 测试完整NLP处理${NC}"
echo "POST $BASE_URL/process"
curl -s -X POST "$BASE_URL/process" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "I cant wait to see you! 😊 Apple Inc. rocks!",
    "language": "auto",
    "enableAll": true
  }' | json_pp || echo ""
echo ""

echo -e "${GREEN}=========================================="
echo "  API测试完成"
echo -e "==========================================${NC}"


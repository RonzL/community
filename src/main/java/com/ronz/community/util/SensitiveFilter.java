package com.ronz.community.util;

import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 敏感词过滤器，将输入的字符串中的敏感词替换为 ***
 * 比如："你好，这里可以赌博，还可以※嫖※娼※" --> "你好，这里可以***，还可以※***※"
 *
 * @Author Ronz
 * @Date 2021/5/17 10:49
 * @Version 1.0
 */

@Component  // 注册到容器中
public class SensitiveFilter {

    // 前缀树根结点
    private final TrieNode rootNode = new TrieNode();
    private static final String REPLACE_SENSITIVE = "***";  // 用于替换敏感词

    /**
     * 读取敏感词文件，生成前缀树
     */
    @PostConstruct  // 在构造方法之后自动执行
    public void generateTrie(){
        // 读取敏感词文件
        try (
                // 圆括号中的输入输出流会在运行完毕后自动关闭
                InputStream is =  this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
        ) {
            String line;
            while((line = br.readLine()) != null){
                // 添加到前缀树
                addTrieNode(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将一行字符串添加到 字典树/前缀树 中
     */
    public void addTrieNode(String line){
        TrieNode pos = rootNode;    // 指向前缀树根结点

        // 遍历敏感词
        for (int i=0; i<line.length(); i++){
            Character cur = line.charAt(i);
            // 先判断当前结点的子结点是否已经包含了当前这个关键字符
            TrieNode subNode = pos.getSubNode(cur);
            if (subNode == null){
                // 如果不包含这个关键字符，则创建该字符对应的结点，把它添加到子结点中
                subNode = new TrieNode();
                pos.addSubNode(cur, subNode);
            }
            // 到了这一步，则确保了当前结点的子结点已经有了当前的关键字符
            // 然后指针往下移动一层，用于之后的字符添加
            pos = subNode;

            // 如果当前结点是关键词最后一个字符，则打上关键词标记
            if (i == line.length()-1){
                subNode.setKeyWordEnd(true);
            }
        }
    }


    /**
     * 给定一个字符串，获取经过过滤之后的字符串
     * 如："你好，这里可以赌博，还可以※嫖※娼※" --> "你好，这里可以***，还可以※***※"
     */
    public String getFilteredStr(String text){
        StringBuilder sb = new StringBuilder(); // 存放过滤后的字符串

        TrieNode subNode = rootNode;    // 指针一，指向字典树
        int left = 0;   // 指针二，字符串左指针
        int right = 0;  // 指针三，字符串右指针

        while(right < text.length()){
            Character ch = text.charAt(right);   // 获取当前字符

            // 判断是否是特殊符号
            if (isSymbol(ch)){
                // 如果是特殊符号，判断第二、第三个指针是不是指向的都是这个字符，也就是说没有字符正在匹配
                if (subNode == rootNode){
                    sb.append(ch);
                    left++;
                }
                // 无论这个特殊符号在关键词的开头还是中间，右指针都要往后移动
                right++;
                continue;
            }

            // 如果不是特殊符号
            subNode = subNode.getSubNode(ch);
            if (subNode == null){
                // 如果为空，则说明这个字符不在敏感词中
                sb.append(text.charAt(left));
                // 左指针往右移动，也就是接下来从下一个字符开始匹配
                left++;
                right = left;
                subNode = rootNode;
            }else if (subNode.isKeyWordEnd()){
                // 如果不为空，且是关键词的最后一个字符，则添加屏蔽字符到过滤后的字符串中
                sb.append(REPLACE_SENSITIVE);
                right++;
                left = right;
                subNode = rootNode;
            }else{
                // 如果不为空且不是关键词的最后一个字符，则右指针往右移动
                right++;
            }
        }

        sb.append(text.substring(left));
        return sb.toString();
    }

    /**
     * 判断是否为其他符号
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树/字典树 的结点
     */
    private class TrieNode{
        private boolean isKeyWordEnd;  // 标识当前结点是否是一个关键词末尾结点
        private Map<Character, TrieNode> subNodes = new HashMap<>();    // 存储子结点

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        /**
         * 添加子结点
         */
        public void addSubNode(Character ch, TrieNode node){
            subNodes.put(ch, node);
        }

        /**
         * 获取子结点
         */
        public TrieNode getSubNode(Character ch){
            return subNodes.get(ch);
        }
    }
}

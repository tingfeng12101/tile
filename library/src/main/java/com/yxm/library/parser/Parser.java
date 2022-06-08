package com.yxm.library.parser;

import com.yxm.library.bean.Game;
import com.yxm.library.bean.GameNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser
{
    // private static final Logger log = LoggerFactory.getLogger(Parser.class);
    private final String originalGame;

    // http://www.red-bean.com/sgf/properties.html
    private static final Set<String> generalProps = new HashSet<String>();

    static
    {
        // Application used to generate the SGF
        generalProps.add("AP");
        // Black's Rating
        generalProps.add("BR");
        // White's Rating
        generalProps.add("WR");
        // KOMI
        generalProps.add("KM");
        // Black Player
        generalProps.add("PB");
        // Black Player
        generalProps.add("PW");
        // Charset
        generalProps.add("CA");
        // File format
        generalProps.add("FF");
        // Game type - 1 means Go
        generalProps.add("GM");
        // Size of the board
        generalProps.add("SZ");
        // Annotator
        generalProps.add("AN");
        // Rules
        generalProps.add("RU");
        // Time limit in seconds
        generalProps.add("TM");
        // How overtime is handled
        generalProps.add("OT");
        // Date of the game
        generalProps.add("DT");
        // Place of the game
        generalProps.add("PC");
        // Result of the game
        generalProps.add("RE");
        // How to show comments
        generalProps.add("ST");
        // How to print move numbers
        generalProps.add("PM");
        // Some more printing magic
        generalProps.add("FG");
        // Name of the game
        generalProps.add("GN");
        // Black territory or area
        generalProps.add("TB");
        // White territory or area
        generalProps.add("TW");
        // Handicap stones
        generalProps.add("HA");
        // "AB": add black stones AB[point list]
        generalProps.add("AB");
        // "AW": add white stones AW[point list]
        generalProps.add("AW");
        // add empty = remove stones
        generalProps.add("AE");
        // PL tells whose turn it is to play.
        generalProps.add("PL");
        // KGSDE - kgs scoring - marks all prisoner stones
        // http://senseis.xmp.net/?CgobanProblemsAndSolutions
        generalProps.add("KGSDE");
        // KGS - score white
        generalProps.add("KGSSW");
        // KGS - score black
        generalProps.add("KGSSB");
    }

    private static final Set<String> nodeProps = new HashSet<>();

    static
    {
        // Move for Black
        nodeProps.add("B");
        // Move for White
        nodeProps.add("W");
        // marks given points with circle
        nodeProps.add("CR");
        // marks given points with cross
        nodeProps.add("MA");
        // marks given points with square
        nodeProps.add("SQ");
        // selected points
        nodeProps.add("SL");
        // labels on points
        nodeProps.add("LB");
        // marks given points with triangle
        nodeProps.add("TR");
        // Number of white stones to play in this byo-yomi period
        nodeProps.add("OW");
        // Number of black stones to play in this byo-yomi period
        nodeProps.add("OB");
        // time left for white
        nodeProps.add("WL");
        // time left for black
        nodeProps.add("BL");
        // Comment
        nodeProps.add("C");
        /*
         * List of points - http://www.red-bean.com/sgf/proplist_ff.html Label
         * the given points with uppercase letters. Not used in FF 3 and FF 4!
         * 
         * Replaced by LB which defines the letters also: Example: L[fg][es][jk]
         * -> LB[fg:A][es:B][jk:C]
         */
        nodeProps.add("L");
    }

    private Stack<GameNode> treeStack = new Stack<GameNode>();

    public Parser(String game)
    {
        originalGame = game;
    }

    public Game parse()
    {
        Game game = new Game();
        // 根节点
        GameNode parentNode = null;

        int moveNo = 1;
        // 遍历originalGame每一个字符
        for (int i = 0; i < originalGame.length(); i++)
        {
            // 获取originalGame每一个字符
            char chr = originalGame.charAt(i);
            // 一个；为开头表示一个节点
            if (';' == chr && (i == 0 || originalGame.charAt(i - 1) != '\\'))
            {
                // 得到节点内容
                String nodeContents = consumeUntil(originalGame, i);
                i = i + nodeContents.length();
                // 得到一个节点
                GameNode node = parseToken(nodeContents, parentNode, game);
                if (node.isMove())
                {
                    node.setMoveNo(moveNo++);
                }

                if (parentNode == null)
                {
                    parentNode = node;
                    // 设置根节点
                    game.setRootNode(parentNode);
                }
                else
                {
                    // 设置子节点
                    parentNode.addChild(node);
                    parentNode = node;
                }
            }
            else if ('(' == chr && parentNode != null)
            {
                // 遇到'('表示一个节点即入栈等待遇到')'出栈
                treeStack.push(parentNode);
            }
            else if (')' == chr)
            {
                if (treeStack.size() > 0)
                {
                    parentNode = treeStack.pop();
                    moveNo = parentNode.getMoveNo() + 1;
                }
            }
            else
            {
            }
        }

        return game;
    }

    private String consumeUntil(String gameStr, int i)
    {
        StringBuffer rtrn = new StringBuffer();
        boolean insideComment = false;
        for (int j = i + 1; j < gameStr.length(); j++)
        {
            char chr = gameStr.charAt(j);
            if (insideComment)
            {
                if (']' == chr && gameStr.charAt(j - 1) != '\\')
                {
                    insideComment = false;
                }
                rtrn.append(chr);
            }
            else
            {
                // C表示坐标备注的的开始，形如B[XX]C[XXXXXXXXX]表示黑子坐标为XX注释为XXXXXXXX
                if ('C' == chr && '[' == gameStr.charAt(j + 1))
                {
                    insideComment = true;
                    rtrn.append(chr);
                    // 遇到）表示某个节点的结束
                }
                else if (';' != chr && ')' != chr && '(' != chr)
                {
                    rtrn.append(chr);
                }
                else
                {
                    break;
                }
            }
        }
        return rtrn.toString().trim();
    }

    private GameNode parseToken(String token, final GameNode parentNode,
            Game game)
    {
        GameNode rtrnNode = new GameNode(parentNode);

        token = prepareToken("'" + token + "'");
        String valuesLB = "";
        // 找到所有节点
        Pattern p = Pattern.compile("([a-zA-Z]{1,})((\\[[^\\]]*\\]){1,})");
        Matcher m = p.matcher(token);
        while (m.find())
        {
            String group = m.group();
            if (group.length() == 0)
                continue;
            // 获取属性
            String key = m.group(1);
            // 获取属性对应的值
            String value = m.group(2);
            // 值的开始为'['结束为']'
            if (value.startsWith("["))
            {
                value = value.substring(1, value.length() - 1);
            }

            // 'AB','AW'表示在棋盘上设置增加黑子或白子属性，棋盘属性对应一个节点
            // AB[xx][xx][xx][xx]AW[xx][xx][xx][xx]表示一个节点
            if ("AB".equals(key) || "AW".equals(key))
            {
                if (game.getProperty("AB") != null && "AB".equals(key))
                {
                    String[] empty = game.getProperty("AB").split(",");
                    String valueEmpty = join2(empty);
                    value = valueEmpty + "[" + value;
                }
                else if (game.getProperty("AW") != null && "AW".equals(key))
                {
                    String[] empty = game.getProperty("AW").split(",");
                    String valueEmpty = join2(empty);
                    value = valueEmpty + "[" + value;
                }
                String[] list = value.split("\\]\\[");
                // ']['连续坐标转成以‘，’为分隔符
                game.addProperty(key, Parser.join(",", list));
            }

            else if (generalProps.contains(key))
            {
                game.addProperty(key, value);
            }
            else if (nodeProps.contains(key))
            {
//                if ("B".equals(key) || "W".equals(key))
//                {
//                    if (game.getProperty("B") != null && "B".equals(key))
//                    {
//                        String[] empty = game.getProperty("B").split(",");
//                        String valueEmpty = join2(empty);
//                        value = valueEmpty + "[" + value;
//                    }
//                    else if (game.getProperty("W") != null && "W".equals(key))
//                    {
//                        String[] empty = game.getProperty("W").split(",");
//                        String valueEmpty = join2(empty);
//                        value = valueEmpty + "[" + value;
//                    }
//                    String[] list = value.split("\\]\\[");
//                    // ']['连续坐标转成以‘，’为分隔符
//                    game.addProperty(key, Parser.join(",", list));
//                }



                if (key.equals("LB"))
                {
                    String[] list = value.split("\\]\\[");

                    if (!valuesLB.equals(""))
                    {
                        valuesLB = valuesLB + "," + Parser.join(",", list);
                    }else{
                        valuesLB = Parser.join(",", list);
                    }

                }
                else
                {
                    rtrnNode.addProperty(key, cleanValue(value));
                }


            }
            else if ("L".equals(key))
            {
            }
            else
            {
            }
        }
        if (!valuesLB.equals(""))
        {
            rtrnNode.addProperty("LB", cleanValue(valuesLB));
        }

        return rtrnNode;
    }

    public static String join(String join, String[] strAry)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++)
        {
            if (i == (strAry.length - 1))
            {
                sb.append(strAry[i]);
            }
            else
            {
                sb.append(strAry[i]).append(join);
            }
        }

        return new String(sb);
    }

    public static String join2(String[] strAry)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++)
        {
            if (i == (strAry.length - 1))
            {
                sb.append(strAry[i] + "]");
            }
            else if (i == 0)
            {
                sb.append(strAry[i] + "][");
            }
            else
            {
                sb.append(strAry[i]).append("][");
            }
        }

        return new String(sb);
    }

    private String cleanValue(String value)
    {
        String cleaned = value.replaceAll("\\\\;", ";");
        return cleaned;
    }

    private String prepareToken(String token)
    {
        token = token.replaceAll("\\\\\\[", "@@@@@");
        token = token.replaceAll("\\\\\\]", "#####");
        return token;
    }
}

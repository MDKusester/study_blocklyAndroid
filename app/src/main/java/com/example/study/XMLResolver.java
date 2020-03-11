package com.example.study;

import android.app.Activity;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLResolver {
    private XMLFunction mFunction;
    private Activity activity;
    private String TAG = "XMLResolver";

    public XMLResolver(Activity activity) {
        this.mFunction = new XMLFunction();
        this.activity = activity;
    }
    public void resolveXML(File xmlFile) {//解析xml文件的入口 启动循环机器

        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //2.创建DocumentBuilder对象
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            Element xmlRoot = document.getDocumentElement();//得到 XML 文档的根节点

            ArrayList<Element> allRootElements = getElementChildren(xmlRoot);//得到节点的子节点
            Element startElement = null;//先存一下,根element里最后执行startElement
            for (Element element : allRootElements) {
                startElement = element;
                if (startElement != null) {
                    break;
                }
            }
            if (startElement == null) {
                return;
            }
            resloveBlockElement(startElement);//开始解析xml
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d(TAG, "resolveXML: 有未解析的指令 出现空指针");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "resolveXML: 未处理异常 : ", e);
        }

    }

    private Object resloveBlockNode(Node elementNode) {//把节点转化为元素的中间桥梁 防止报错
        try {
            Element element = (Element) elementNode;
            return resloveBlockElement(element);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private XMLDataRequest resloveBlockElement(Element block) throws InterruptedException {//处理block元素 一堆case
        Element element;//待用
        int direction, speed, time;
        String returnState;

        if (Thread.interrupted()) {
            Log.d(TAG, "xml解析执行被中断");
            return null;
        }
        switch (getElementType(block)) {
            case "control_wait":
                element = getElementChildrenByName(block, "control_time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.wait(time);
                break;
            case "move_go_back_time":
                element = getElementChildrenByName(block, "go_or_back");
                direction = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "move_speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "move_time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.goBackTime(direction, speed, time);
                break;
            case "move_stop":
                mFunction.stop();
                break;
            case "state_led":
                returnState = mFunction.stateLed();
                return new XMLDataRequest(returnState);
            case "controls_if": {
                Log.d(TAG, "------------------controls_if 模块---------------------");
                Element ifElement = getElementChildrenByName(block, "IF0");
                String ifResult = resloveValueElement(ifElement).getRequestString();//处理if的条件语句并得到字符型结果

                Log.d(TAG, "controls_if判断结果:" + ifResult);
                if (ifResult.equals("TRUE")) {
                    Log.d(TAG, "contrlos_if执行");
                    Element do0Element = getElementChildrenByName(block, "DO0");
                    if (do0Element != null)
                        resloveStatementElement(do0Element);
                } else {
                    Log.d(TAG, "contrlos_if不执行,跳过");
                }
                Log.d(TAG, "------------------controls_if 模块---------------------");
            }
            break;
            case "controls_ifelse": {
                Log.d(TAG, "-----------------controls_ifelse 模块-------------------");
                Element ifElement = getElementChildrenByName(block, "IF0");
                String ifResult = resloveValueElement(ifElement).getRequestString();
                Log.d(TAG, "controls_ifelse判断结果:" + ifResult);
                if (ifResult.equals("TRUE")) {
                    Log.d(TAG, "controls_ifelse执行DO");
                    Element do0Element = getElementChildrenByName(block, "DO0");
                    if (do0Element != null)
                        resloveStatementElement(do0Element);
                } else {
                    Log.d(TAG, "controls_ifelse执行ELSE");
                    Element elseElement = getElementChildrenByName(block, "ELSE");
                    if (elseElement != null)
                        resloveStatementElement(elseElement);
                }
                Log.d(TAG, "-------------------controls_ifelse 模块-----------------");
            }
            break;
            case "controls_repeat_ext": {
                Log.d(TAG, "---------------------controls_repeat_ext 模块---------------------");

                Element timeElement = getElementChildrenByName(block, "TIMES");
                Element doElement = getElementChildrenByName(block, "DO");
                XMLDataRequest repeatTimes = resloveValueElement(timeElement);
                int repeatTime = Integer.parseInt(repeatTimes.getRequestString());
                for (int i = 0; i < repeatTime; i++) {

                    Log.d(TAG, "controls_repeat_ext模块重复" + i + "次");
                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }

                    Thread.sleep(200);
                }
                Log.d(TAG, "---------------------controls_repeat_ext 模块---------------------");
            }
            break;
            case "controls_whileUntil": {
                Log.d(TAG, "--------------------controls_whileUntil 模块---------------------");
                Element modeElement = getElementChildrenByName(block, "MODE");
                Element boolElement = getElementChildrenByName(block, "BOOL");
                Element doElement = getElementChildrenByName(block, "DO");
                String mode = resloveFieldElement(modeElement).getRequestString();
                String bool = resloveValueElement(boolElement).getRequestString();
                while (mode.equals("WHILE") && bool.equals("TRUE")) {
                    Log.d(TAG, "controls_whileUntil 模块执行 WHILE 功能");

                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }

                    Thread.sleep(200);
                }
                while (mode.equals("UNTIL") && !bool.equals("TRUE")) {
                    Log.d(TAG, "controls_whileUntil 模块执行 UNTIL 功能");

                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }
                    Thread.sleep(200);
                }
                Log.d(TAG, "--------------------controls_whileUntil 模块---------------------");
            }
            break;
            default:
                break;
        }

        //处理next模块
        NodeList blockNodeList = block.getChildNodes();
        for (int i = 0; i < blockNodeList.getLength(); i++) {
            Node node = blockNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("next")) {
                    resloveNextElement(node);
                }
            }
        }
        return null;
    }

    private void resloveNextElement(Node block) {//处理next中所有子节点 通常只有一个节点
        NodeList nextNodeList = block.getChildNodes();
        for (int i = 0; i < nextNodeList.getLength(); i++) {
            Node shadowNode = nextNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                resloveBlockNode(shadowNode);//先要判断是否为element节点
            }
        }
    }

    private XMLDataRequest resloveValueElement(Element block) throws InterruptedException {//处理条件逻辑判断语句 value标签

        ArrayList<Element> elements = getElementChildren(block);

        for (int i = elements.size() - 1; i >= 0; i--) {
            //从后往前循环,第一位可能是默认值
            Element valueNode = elements.get(i);
            switch (valueNode.getNodeName()) {
                case "shadow":
                    return resloveShadowElement(valueNode);

                case "block":
                    return resloveBlockElement(valueNode);//发现if后是一个语句 则回调去执行

                default:
                    Log.d(TAG, "resloveValueElement: 未知的 Value node名称:" + valueNode.getNodeName());
            }
        }

        return new XMLDataRequest();
    }

    private XMLDataRequest resloveShadowElement(Element block) {//处理()逻辑条件判断语句中的shadow类型 shadow标签
        Log.d(TAG, "shadow: " + "   Type:" + getElementType(block));
        switch (getElementType(block)) {
            case "math_number":
                ArrayList<Element> numberElements = getElementChildren(block);
                for (Element element : numberElements) {
                    return resloveFieldElement(element);
                }
                break;
            default:
                Log.d(TAG, "resloveShadowElement: 未知的shaow模块" + getElementType(block));
                break;
        }
        return new XMLDataRequest();
    }

    private XMLDataRequest resloveFieldElement(Element block) {//（）逻辑判断中shadow中的类型 field标签

        switch (getElementName(block)) {
            case "go_or_back":
            case "move_speed":
            case "move_time":
                return new XMLDataRequest(block.getFirstChild().getNodeValue());//得到文本节点并转化为字符串
            case "NUM":
            case "MODE":
            case "BOOL":
                return new XMLDataRequest(block.getFirstChild().getNodeValue());
            default:
                Log.d(TAG, "resloveFieldElement: 未知field模块 " + getElementName(block));
                return new XMLDataRequest(block.getFirstChild().getNodeValue());
        }
    }

    private void resloveStatementElement(Element block) {//处理逻辑判断后的do语句 statement标签
        ArrayList<Element> elementChildren = getElementChildren(block);
        for (int i = 0; i < elementChildren.size(); i++) {
            Node shadowNode = elementChildren.get(i);
            resloveBlockNode(shadowNode);
        }
    }


    /*
     *自定义了部分DOM操作
     */
    private String getElementType(Element element) {
        return element.getAttribute("type");
    }

    private String getElementType(Node node) {
        Element element = (Element) node;
        try {
            return element.getAttribute("type");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getElementName(Element element) {
        return element.getAttribute("name");
    }

    private String getElementName(Node node) {
        try {
            Element element = (Element) node;
            return element.getAttribute("name");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取全部子element
    private ArrayList<Element> getElementChildren(Element block) {

        ArrayList<Element> childrenElements = new ArrayList<>();
        NodeList shadowNodeList = block.getChildNodes();
        for (int i = 0; i < shadowNodeList.getLength(); i++) {
            Node shadowNode = shadowNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                childrenElements.add((Element) shadowNode);
            }
        }
        return childrenElements;
    }


    private Element getElementChildrenByName(Element block, String name) {//根据name属性 获取子block
        NodeList shadowNodeList = block.getChildNodes();
        for (int i = 0; i < shadowNodeList.getLength(); i++) {
            Node shadowNode = shadowNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                if (getElementName(shadowNode).equals(name)) {
                    return (Element) shadowNode;
                }
            }
        }
        if (name.equals("DO") || name.equals("DO0") || name.equals("ELSE")) {//为了找blockly里的元素 而不是找逻辑运算中的blockly 所以返回null
            return null;
        }
        return null;
    }

}

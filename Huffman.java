package Misson6_bing;
import 实验3.KeyInput;

import java.awt.event.KeyEvent;
import java.io.*;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    private Map<Character,Integer> map=new HashMap<>();//存元素的哈希表
    private Map<Character,String> ce=new HashMap<>();//密码本哈希表
    private PriorityQueue<Tree> trees=new PriorityQueue<>();//无界优先级队列
    private String source;

    public void init(String source){
        this.source=source;
        char[] chars= source.toCharArray();//String转char
        for (char c :chars){//迭代
            if (!map.containsKey(c)){//若无当前元素 放入map
                map.put(c,1);
            }else {
                map.put(c,map.get(c)+1);//有 则计数+1
            }
        }
        afterInit();
    }

    private void afterInit() {
        map.forEach((c,count)->{//迭代送入树中->按优先级排序
            Node<Character> node=new Node<>();
            node.key=count;
            node.charData=c;
            Tree tree=new Tree();
            tree.setRoot(node);
            trees.add(tree);
        });
    }

    public void build(){
        while (this.trees.size()>1){//取两个加一个，队列中只剩下一个元素时结束
            Tree left=this.trees.poll();//队首获取元素，同时获取的这个元素将从原队列删除
            Tree right=this.trees.poll();//队首获取元素，同时获取的这个元素将从原队列删除
                                           //每次从队首获取的两个元素是最小的两个，第二次开始获取的元素有一个是原本队列中的，
                                         // 另一个是新add进来的上一次的根节点，值是上一次两个子树的值的和
            Node node=new Node();
            node.key=left.getRoot().key+right.getRoot().key;//根结点的值为左右子树之和
            node.leftChild=left.getRoot();
            node.rightChild=right.getRoot();
            left.setRoot(node);
            this.trees.add(left);
        }
    }
    public void encoding(){
        Tree tree=this.trees.peek();//查看不移除
        this.encoding(tree.getRoot(),"");
        ce.forEach((k,v)->{
            if(v.length()<8){
               // System.out.print(v+" ");
                String s1=String.format("%08d",Integer.valueOf(v) );//valueOf将int/String转化为Integer包装类型
               // System.out.println(s1);
                ce.put(k,s1);
            }
        });
    }
    public String encodingResult(){
        StringBuilder sb=new StringBuilder();
        char[] chars= source.toCharArray();
        for (char c :chars){
            sb.append(ce.get(c));
        }
        return sb.toString();
    }
    private void encoding(Node<Character> node,String encoding){
        if (node!=null){
            if (node.leftChild==null && node.rightChild==null){
                ce.put(node.charData,encoding);
              //  System.out.println(encoding);
            }
            encoding(node.leftChild,encoding+"0");
            encoding(node.rightChild,encoding+"1");
        }
    }
    public void displayTree(){
        Tree tree=this.trees.peek();
        tree.inDisplay(tree.getRoot());
    }
    public void displayEncodeing(){
        ce.forEach((k,v)->{
            System.out.println(k+":"+v);
        });
    }
    public String decoding(String encodeStr,Map<Character,String> encodeMap) throws IOException {//解码
        StringBuilder decodeStr = new StringBuilder();
        while(encodeStr.length()>1){//主循环，在解码未结束时一直存在
            for(Map.Entry<Character,String> e: encodeMap.entrySet()){//遍历Map中所有键和值；Map.Entry是Map的一个内部接口，entrySet返回值就是这个map中各个键值对映射关系的集合
                String charEncodeStr = e.getValue();//获得元素的值
                if(encodeStr.startsWith(charEncodeStr)){//检测是否以指定值开始,
                    decodeStr.append(e.getKey());//解码的表中加入键
                    encodeStr = encodeStr.substring(charEncodeStr.length());//让密码字串从结束位置开始
                    break;
                }
            }
        }
        return decodeStr.toString();
    }
    /*
    * 创建/读取文件
    * */
    public static boolean creatFile(String filePath) throws IOException {
        boolean flag=false;
        File newF=new File(filePath);
        if(!newF.exists()){
            try {
                newF.createNewFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            flag=true;
        }
        return flag;
    }
    /*
    从文件中读取字符串 读取String类型文件
    * */
    public static String readTextFile_FR(String filePath) throws IOException {
        String thisLine=null;
        String result="";
        creatFile(filePath);
        File file=new File(filePath);
        if(file.exists()&&file.isFile()){
            try {
                BufferedReader br=new BufferedReader(new FileReader(filePath));
                while((thisLine=br.readLine())!=null){
                    result+=thisLine+"\n";
                }
                br.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
    字节类型写入文件
    * */
    public static boolean write_byte_file(String content,String filePath,boolean append){
        boolean flag=false;
        char[] chars=content.toCharArray();
        byte[] bytes = new byte[chars.length];
        int i=0;
        for(char c:chars){
            if(c=='\n'){
                break;
            }
            int t=Integer.parseInt(String.valueOf(c));

           bytes[i]=(byte)t;
           i++;
       }
        File thisFile=new File(filePath);
        try {
            if(!thisFile.exists()){
                thisFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePath);
            //System.out.println(filePath+bytes);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag=true;

        return flag;
    }
    /*
    按字节读取文件内容
    * */
    public static byte[] read_byte_file(String filePath) throws IOException {

        creatFile(filePath);
        File file=new File(filePath);
        try {
            FileInputStream in =new FileInputStream(new File(filePath));
            //当文件没有结束时，每次读取一个字节显示
            byte[] data=new byte[in.available()];
            in.read(data);
            in.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    压缩String文件内容为byte[]类型
        * */
    public static boolean compressFile_CF(String filePath_r,String filePath_w,boolean append) throws IOException {
        boolean flag=false;
        creatFile(filePath_w);
        String content=readTextFile_FR(filePath_r);//从密码本中读密码
        StringBuilder content_cod=new StringBuilder();
        int j=0;
        for(int i=0;i<content.length();i+=8){
            if((8*(j+1)-1)>content.length()){//长度超过字符串长度则弹出 不然就溢出
                break;
            }
            int keys=Integer.parseInt(content.substring(8*j,(8*(j+1)-1)),2);//每8位取一次数,并二进制转十进制
             content_cod.append(keys);//每取一个数就加进去
            // System.out.println(content_cod);
            j++;
        }
        System.out.println(content_cod);
        write_byte_file(content_cod.toString(),filePath_w,false);
        flag=true;
        System.out.println("压缩比 "+content.length()+":"+content_cod.length());
        return flag;
    }
    /*
    * 解压文件
    * */
    public static boolean decompressFile_DF(String filePath_com) throws IOException {
            boolean flag=false;

            byte[] bytes=read_byte_file(filePath_com);
            StringBuilder content_decom=new StringBuilder();//解码字符串
            StringBuilder content_com=new StringBuilder();//压缩之后的码
          for(byte b:bytes){//迭代byte强转int转化为StringBuilder
              int t=(int)b;
              content_com.append(String.valueOf(t));
          }
            char[] chars=content_com.toString().toCharArray();//将压缩码依次拿出来
            for(char c:chars){//迭代依次转化压缩码
                if(c=='\n'){
                    break;
                }
                int temp=Integer.parseInt(String.valueOf(c));//单个字符依次转化为字符串再进行转化为二进制操作

               String s1=String.format("%08d",Integer.valueOf(Integer.toBinaryString(temp)).intValue());
               //System.out.println(s1);
               content_decom.append(s1);
            }
            writeTextFile_FW(content_decom.toString(),filePath_com,false);
            flag=true;
            return flag;

    }
    /*
    * 向文件中写入String类型数据
    * */
    public static boolean writeTextFile_FW(String content,String filePath,boolean append){
        boolean flag=false;
        File thisFile=new File(filePath);
        try {
            if(!thisFile.exists()){
                thisFile.createNewFile();
            }
            BufferedWriter bw=new BufferedWriter(new FileWriter(filePath,append));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag=true;
        return flag;
    }
    public static void main(String[] args) throws IOException {
        Huffman huffman=new Huffman();
        int fun=-1;
        while(fun!=0){
            System.out.println("***************************************");
            System.out.println("*   1.选择/创建需要进行编码的文件        *");
            System.out.println("*   2.建立哈夫曼树                      *");
            System.out.println("*   3.建立密码本并对文件编码             *");
            System.out.println("*   4.选择需要解码的文件并解码           *");
            System.out.println("*   5.按位压缩方式对文件进行压缩          *");
            System.out.println("*   6.解压压缩文件                      *");
            System.out.println("*************************************\n");
            fun= KeyInput.readInt();
            String compress_file="D:\\\\数据结构\\\\文件存放处\\\\The compress_code.cod";
            switch (fun){
                default:
                    System.out.println("No such function found,try again!");
                    System.in.read();
                case 1:
                   System.out.println("1.D:\\\\数据结构\\\\文件存放处\\\\Test1.txt");
                   System.in.read();
                   creatFile("D:\\\\数据结构\\\\文件存放处\\\\Test1.txt");
                   System.out.println("选择功能\n1.向文件中写入内容\n2.直接读取");
                   int fun1=KeyInput.readInt();
                   if(fun1==1){
                       boolean append=true;
                       System.out.println("1.选择追加\n2.选择覆盖");
                       int ap=KeyInput.readInt();
                       if(ap==2){
                           append=false;
                       }
                       System.out.println("请输入你要写入文件的字符串");
                       String s1=KeyInput.readString();
                       writeTextFile_FW(s1,"D:\\\\数据结构\\\\文件存放处\\\\Test1.txt",append);
                   }
                   else{
                       String s=readTextFile_FR("D:\\\\数据结构\\\\文件存放处\\\\Test1.txt");
                       System.out.println(s);
                   }
                   break;
                case 2:
                    String source=readTextFile_FR("D:\\\\数据结构\\\\文件存放处\\\\Test1.txt");
                    System.out.println("文件内容为: "+source);
                    huffman.init(source);
                    huffman.build();
                    huffman.displayTree();
                    break;
                case 3:
                    huffman.encoding();
                    huffman.displayEncodeing();
                    System.out.println( huffman.encodingResult());
                    try{
                        writeTextFile_FW(huffman.encodingResult(),"D:\\\\数据结构\\\\文件存放处\\\\The code.cod",false);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    String s1=null;
                    s1=readTextFile_FR("D:\\\\数据结构\\\\文件存放处\\\\The code.cod");
                    System.out.println(s1);
                    String decode="";
                    decode=huffman.decoding(s1,huffman.ce);
                    System.out.println(decode);
                    break;
                case 5:
                    System.out.println("开始压缩...");
                    long start=System.currentTimeMillis();

                    compressFile_CF("D:\\\\数据结构\\\\文件存放处\\\\The code.cod",compress_file,false);
                    System.out.println("压缩结束...用时:"+(System.currentTimeMillis()-start));
                    break;
                case 6:
                    System.out.println("开始解压...");
                    long start1=System.currentTimeMillis();
                    //String decompress_file="D:\\\\数据结构\\\\文件存放处\\\\decompress_code.cod";
                     decompressFile_DF(compress_file);
                   //  writeTextFile_FW("D:\\\\数据结构\\\\文件存放处\\\\The code.cod",false);
                    System.out.println("解压结束...用时:"+(System.currentTimeMillis()-start1));
                    break;
                case 0:
                    break;
            }

        }
    }
}

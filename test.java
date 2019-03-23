import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class test {

    public String cadena = "";
    private int indiceDeCadena = -1;

    Stack <String> pila = new Stack<String>();

    String [][] tablaLL1 =

            {
                    { "E$", null, null, "E$", null, null},
                    { "TK", null, null, "TK", null, ""},
                    { null, "+TK", null, null, "", ""},
                    { "FH", null, null, "FH", null, null},
                    { null, "", "*FH", null, "", ""},
                    { "a", null, null, "(E)", null, null},
            };

    String [] noTerminales = {"G","E","K","T","H","F"};
    String [] terminales = {"a", "+", "*", "(", ")", "$"};

    public test(String in) {

        this.cadena = in;
                try
                {

                    //csv file containing data
                    String strFile = "C:/java/entrada.csv";

                    //create BufferedReader to read csv file
                    BufferedReader br = new BufferedReader( new FileReader(strFile));
                    String strLine = "";
                    StringTokenizer st = null;
                    String[] noTerminales = new String [100];
                    String[] terminales = new String [100];
                    String[][] tablaLL1 = new String [100][100];
                    String temp;
                    int lineNumber = 0, tokenNumber = 0;

                    //read comma separated file line by line
                    while( (strLine = br.readLine()) != null)
                    {
                        //break comma separated line using ","
                        st = new StringTokenizer(strLine, ",");

                        //reset token number
                        tokenNumber = 0;

                        switch (lineNumber) {

                            case 0:
                                // llenar arreglo de NoTerminales
                                while(st.hasMoreTokens()) {
                                    noTerminales[tokenNumber]= st.nextToken();

                                    System.out.println("Line " + lineNumber +", Token # " + tokenNumber + ", No Terminal : "+ noTerminales[tokenNumber]);

                                    tokenNumber++;  //prepara para que en el siguiente loop agarra siguiente token
                                }
                                break;


                            case 1:
                                // llenar arreglo de Terminales
                                while(st.hasMoreTokens()) {
                                    terminales[tokenNumber]= st.nextToken();

                                    System.out.println("Line  " + lineNumber +", Token # " + tokenNumber + ", Terminal : "+ terminales[tokenNumber]);

                                    tokenNumber++;  //prepara para que en el siguiente loop agarra siguiente token
                                }
                                break;

                            default:
                                // llenar Tabla LL1
                                System.out.println("ESTOY DENTRO");
                                System.out.println(lineNumber);
                                if (lineNumber > 2) {
                                    //if (noTerminales[lineNumber - 2] == st.nextToken()) {  //revisa si el primer token es efectivamente el NoTerminal que usamos: Check de sincronicidad
                                    //no usamos el primer token porque es el No Terminal de la primera columna:
                                    temp = st.nextToken();
                                    while(st.hasMoreTokens()) {
                                        tablaLL1[lineNumber-3][tokenNumber] = st.nextToken();
                                        System.out.println("Line # " + lineNumber +", Token # " + tokenNumber + ", Token : "+ tablaLL1[lineNumber-3][tokenNumber]);
                                        tokenNumber++;  //prepara para que en el siguiente loop agarra siguiente token
                                    }
                                }
                                //}
                                break;

                        }
                        lineNumber++;

                    }
			/*
			for (int i=0; i < 7; i++) {
				System.out.println("i = " + i + " tiene adentro:" + terminales[i]);
				System.out.println("i = " + i + " tiene adentro:" + noTerminales[i]);
				for (int j=0; j < 7; j++) {
					System.out.println("i = " + i + " j = " + j + " tiene adentro:" + tablaLL1[i][j]);
				}
			}
			*/
                }
                catch(Exception e)
                {
                    System.out.println("Exception while reading csv file: " + e);
                }


            }



    private void pushProduccion (String produccion) {

        for(int i = produccion.length()-1; i>=0; i--) {
            // String str = tablaLL[getIndiceNoTerminalDePila()][getIndiceTerminaldeCadena()]
            char ch = produccion.charAt(i);
            String str = String.valueOf(ch);
            pushPila(str);

            //getIndiceNoTerminalDePila()
            //for i = 0 hasta final de la lista{
            //if
            //}
            //return i;
        }
    }


    public void parser()
    {

        pushPila(noTerminales[0]); 					//insertar simbolo No Terminal Incial

        String token=getTokenDeCadena();  		//Lee token de la cadena de entrada

        String top=null;



        do
        {
            System.out.println(Arrays.toString(pila.toArray()));
            top=this.pop();
            if(isNonTerminal(top)) {
                String rule=this.getProduccion(top, token);
                this.pushProduccion(rule);
            }
            else if(isTerminal(top)) {
                if(!top.equals(token)) {
                    error("this token is not corrent , By Grammer rule . Token : ("+token+")");
                }
                else {
                    System.out.println("Matching: Terminal :( "+token+" )");
                    token =getTokenDeCadena();
                    //top=pop();
                }
            }
            else {
                error("Never Happens , Because top : ( "+top+" )");
            }

            if(token.equals("$")){
                break;
            }
            //if top is terminal

        } while(true);			//out of the loop when $

        if(token.equals("$")) {	// entonces cadena de entrada es aceptada
            System.out.println("Input is Accepted by LL1");
        }
        else {
            System.out.println("Input is not Accepted by LL1");
        }
    }

    private boolean isTerminal(String s) {
        for(int i=0;i<this.terminales.length;i++) {
            if(s.equals(this.terminales[i])) {
                return true;
            }
        }

        return false;
    }

    private boolean isNonTerminal(String s) {
        for(int i=0;i<this.noTerminales.length;i++) {
            if(s.equals(this.noTerminales[i])) {
                return true;
            }
        }
        return false;
    }

    private String getTokenDeCadena() {
        indiceDeCadena++;
        char ch=this.cadena.charAt(indiceDeCadena);
        // string = this.tokens[indiceDeCadena];
        String str=String.valueOf(ch);

        return str;
    }

    private void pushPila(String s) {
        this.pila.push(s);
    }

    private String pop() {
        return this.pila.pop();
    }

    private void error(String message) {
        System.out.println(message);
        throw new RuntimeException(message);
    }

    public String getProduccion(String non,String term) {
        int row=getnonTermIndex(non);
        int column=getTermIndex(term);

        String rule=this.tablaLL1[row][column];
        if(rule==null) {
            error("There is no Rule by this , Non-Terminal("+non+") ,Terminal("+term+") ");
        }
        return rule;
    }

    private int getnonTermIndex(String non) {
        for(int i=0;i<this.noTerminales.length;i++) {
            if(non.equals(this.noTerminales[i])){
                return i;
            }
        }
        error(non +" is not NonTerminal");
        return -1;
    }

    private int getTermIndex(String term) {
        for(int i=0;i<this.terminales.length;i++) {
            if(term.equals(this.terminales[i])) {
                return i;
            }
        }
        error(term +" is not Terminal");
        return -1;
    }

    //main
    public static void main(String[] args) {
        test parser=new test("a+(a+a*(a)+a)$");//i*i+(i+i)$
        parser.parser();

    }

}




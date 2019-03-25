import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.swing.JOptionPane;




public class test {

    public String temp = "";
    public String cadena[];
    public String epsilon = "¬";
    
    private int indiceDeCadena = -1;
    private int indiceDeResultados = 0;
    private boolean errorFlag = false;
    Stack <String> pila = new Stack<String>();

    public String[] noTerminales = new String [100];
	public String[] terminales = new String [100];
	public String[][] tablaLL1 = new String [100][100];
//	public String[][] tablaResultados = new String[100][100];
	public List<List<String>> tablaResultados = new ArrayList<List<String>>();	
/*	tablaResultados.get(0).set(0, "COINCIDENCIA");
	tablaResultados.add("COINCIDENCIA")
	tablaResultados.add("COINCIDENCIA")
	tablaResultados.add("COINCIDENCIA")*/
//	public static String[] myList = {"COINCIDENCIA", "PILA","CADENA","ACCION"};
//	public static ArrayList<ArrayList<String>> splitList(String[] list);

	
	
	
    public test(String[] in) {
        this.cadena = in;    
        initDatosEntrada();
    }
    
    private void initDatosEntrada() {
    	try
    	{
		
	    	//archivo que contiene datos de entrada (terminales, no terminales, tabla ll1)
	    	String strFile = "C:/java/entrada2.txt";
	    	
	    	BufferedReader br = new BufferedReader( new FileReader(strFile));
	    	String strLine = "";
	    	String temp;
	    	int lineNumber = 0, tokenNumber = 0;    	
	    	StringTokenizer st = null;
		
	    	//read comma separated file line by line
	    	while( (strLine = br.readLine()) != null)
	    	{
	    		//break comma separated line using ","
	    		st = new StringTokenizer(strLine, ",");
	    		
	    		//reset token number
	    		tokenNumber = 0;
	    		
	    		switch (lineNumber) {
	    			
	    			case 0:		// cargar arreglo NoTerminales
	    				while(st.hasMoreTokens()) {
	    					noTerminales[tokenNumber]= st.nextToken();
	    					tokenNumber++;
	    				}
	    				break;
	    				
	
	    			case 1: 	// cargar arreglo Terminales
	    				while(st.hasMoreTokens()) {
	    					terminales[tokenNumber]= st.nextToken();							
	    					tokenNumber++;
	    				}
	    				break;
	
	    			default:  	// cargar Tabla LL1
	    				if (lineNumber > 2) {
	    					//if (noTerminales[lineNumber - 2] == st.nextToken()) {  //revisa si el primer token es efectivamente el NoTerminal que usamos: Check de sincronicidad
	    					
	    					//no usamos el primer token porque es el No Terminal de la primera columna:
	    					temp = st.nextToken();
	    					
	    					while(st.hasMoreTokens()) {
	    						tablaLL1[lineNumber-3][tokenNumber] = st.nextToken();
//	    						System.out.println("Line # " + lineNumber +", Token # " + tokenNumber + ", Token : "+ tablaLL1[lineNumber-3][tokenNumber]);								
	    						tokenNumber++;
	    					}							
	    				}
	    				//}
	    				break;	
	    		}
	    		lineNumber++;								
	    	}
		}
		catch(Exception e)
		{
			System.out.println("Error al leer archivo de texto: " + e);			
		}
    }

    public void addRowResultados (boolean init) {
    	if (init) {
    		tablaResultados.add(0, new ArrayList<String>());
            tablaResultados.get(0).add(0, "COINCIDENCIA");
    		tablaResultados.get(0).add(1, "PILA");
    		tablaResultados.get(0).add(2, "CADENA");
    		tablaResultados.get(0).add(3, "ACCION");
    	}
    	indiceDeResultados++;
    	tablaResultados.add(indiceDeResultados, new ArrayList<String>());
    	tablaResultados.get(indiceDeResultados).add(0, "");
    	tablaResultados.get(indiceDeResultados).add(1, "");
    	tablaResultados.get(indiceDeResultados).add(2, "");
    	tablaResultados.get(indiceDeResultados).add(3, "");    	
    }
    
    
    //columnas: 0 Coincidencia | 1 Pila | 2 Entrada | 3 Accion
    public void addResultado (int column,String resultado, boolean newRow) {
    	if (newRow) {
    		addRowResultados(false);
    	}

    	tablaResultados.get(indiceDeResultados).set(column, resultado);
    }
    
    public String rpad(String inStr, int finalLength)
    {
        return (inStr + "                               ").substring(0, finalLength);
    }
    
    private void printResultados () {
    	String line;
    	for (int i=0; i < tablaResultados.size(); i++) {
    		line = rpad(tablaResultados.get(i).get(0),30)+"   "+rpad(tablaResultados.get(i).get(1),30)+"   "+rpad(tablaResultados.get(i).get(2),30)+"   "+rpad(tablaResultados.get(i).get(3),17);
       		System.out.println(line);
    	}
    		
    }


    private void pushProduccion (String produccion) {
    	// en orden reversa metemos los tokens del cuerpo de produccion a la pila
    	String arr[] = produccion.split(" ");
  		for(int i = arr.length-1; i>=0; i--) {
        	pushPila(arr[i]);                                    
        }
    }
    

    public void parser()
    {
        
    	pushPila("$"); 								//insertar simbolo final de la pila
        pushPila(noTerminales[0]); 					//insertar simbolo No Terminal Incial
        addRowResultados(true);        
        
        String token= getTokenDeCadena(false);  	//Lee token de la cadena de entrada
        String top= ""	;							//top de la Pila
        String coincidencia= "";

        do
        {   
        	//prepara resultados en la tabla, menos la columna de ACCION
        	addResultado(0,coincidencia,false);
        	addResultado(1,pila.toString(), false);
        	addResultado(2,Arrays.toString(cadena), false);
            
        	//obten token de la pila
        	top=this.pop();
            if(isNonTerminal(top)) {  							//encontramos un No Terminal
                String cuerpo=this.getProduccion(top, token); 	//obten cuerpo de produccion de la tabla LL1
                this.pushProduccion(cuerpo); 					//pon cuerpo en la pila
                if (!errorFlag) {
                	addResultado(3,"salida "+top+"->"+cuerpo,true); //reporta la accion en la tabla de resultados
                }
            }

            else if(top.equals("$")){		//pila esta vacia
            	if(token.equals("$")) {		//cadena tambien llego al final
                    System.out.println("Cadena de entrada es ACEPTADA!");
                }
                else {
                    System.out.println("Cadena de entrada fue RECHAZADA!");
                }
                break;
            }

            else if(isTerminal(top)) {							//encontramos un simbolo terminal
                if(top.equals(token)) {							//coincide con el token de la cadena de la entrada
                	coincidencia = coincidencia + top;			//agrega al string de coincidencias
                	addResultado(3,"coincide "+top,true);		//reporta la acción en la tabla de resultados
                    token=getTokenDeCadena(true);  					//obten siguiente token de la cadena de entrada
                }
                else {											//simbolo terminal no coincide con token de la cadena de entrada
                    error("Simbolo terminal esperado: "+top+", leimos: "+ token);                	
                }
            }
            else if(isEpsilon(top)) {							//tenemos un epsilon en nuestras manos
  //          	addResultado(3,"salida ->"+top,true);			//reporta la accion en la tabla de resultados
            	//no hacemos nada más, automaticamente habrá un pop al reentrar al do-loop, quitando el simbolo NoTerminal anterior de la pila.
            }

            else {
                error("Encontramos un simbolo en la tabla LL1 que no pertenece a la gramatica: ( "+top+" )");
            }
            
            if (errorFlag) {	 //hubo algún error, vamos a salir del loop
            	break;
            }
        } while(true);		//solo sale del loop por error, or por haber encontrado $ en la pila
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
    
    private boolean isEpsilon(String s) {
        if (s.equals(epsilon)) {        
        	return true;
        }
        return false;
    }

    public String getTokenDeCadena(boolean pop) {
    	if (pop) cadena[indiceDeCadena] = "";
        indiceDeCadena++; 
        return cadena[indiceDeCadena];

    }

    private void pushPila(String s) {
        this.pila.push(s);
    }

    private String pop() {
        return this.pila.pop();
    }

    private void error(String message) {
        System.out.println(message);
        errorFlag= true;
    }

    public String getProduccion(String non,String term) {
        int row=getnonTermIndex(non);
        int column=getTermIndex(term);
        String cuerpo= "";
        if (column < 0) {  
            error("token "+ term +" de la cadena de entrada no es parte de los terminales de la gramatica");
        	addResultado(3,"Error!",true);
        }
        else if (row < 0) {
        	error("simbolo "+ non +" de la pila no es un simbolo No-Terminal, hay un error en la tabla LL1 o en la lista de No-Terminales");
        	addResultado(3,"Error!",true);
        }
        else {
        	cuerpo=this.tablaLL1[row][column];
        	if(cuerpo.equals("null")) {
        		addResultado(3,"Error!",true);
        		error("Cadena RECHAZADA, no hay regla de produccion para No terminal("+non+") y terminal("+term+") ");
        	}
        }
    	return cuerpo;
    }

    private int getnonTermIndex(String non) {
        for(int i=0;i<this.noTerminales.length;i++) {
            if(non.equals(this.noTerminales[i])){
                return i;
            }
        }        
        return -1;
    }

    private int getTermIndex(String term) {
        for(int i=0;i<this.terminales.length;i++) {
            if(term.equals(this.terminales[i])) {
                return i;
            }
        }
        return -1;
    }

    //main
    public static void main(String[] args) {    	
    	
    	String str = JOptionPane.showInputDialog("expresión");
    	String[] tokens = str.split(" ");
    	test parser = new test(tokens);
        parser.parser();
        parser.printResultados();
    }

}




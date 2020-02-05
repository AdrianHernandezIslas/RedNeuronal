/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redneuronal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author Adrian
 */
public class RedNeuronal implements Serializable{
    public ArrayList<ArrayList<Neurona>> red;
    private int cantidadCapasOcultas,cantidadNeuronasCapaSalida;
    private int cantidadNeuronasCapa[];
    
    public RedNeuronal() {
        red = new ArrayList<>();
    }
    
    private void crearCapaEntrada(ArrayList<Double> entradas){
        ArrayList<Neurona> capa = new ArrayList<>();
        ArrayList<Double> aux = null;
        
        for (int i = 0; i < entradas.size(); i++) {
             aux = new ArrayList<>();
             aux.add(entradas.get(i));
             capa.add(new Neurona(aux, false, true));  
        }    
        red.add(capa);
    }
    
    private void crearCapasOcultas(){
        ArrayList<Neurona> capa = new ArrayList<>();
        ArrayList<Double> entradas = null;
        for (int i = 0; i < cantidadCapasOcultas ; i++) {
            entradas = new ArrayList<>();
            for (int j = 0; j < red.get(red.size()-1).size(); j++) {
                entradas.add(red.get(red.size()-1).get(j).calcular());
            }
            
            for (int k = 0; k < cantidadNeuronasCapa[i] ; k ++) {
               capa.add(new Neurona((ArrayList<Double>)entradas.clone(), false, false));
            }
            red.add(capa);
            capa = new ArrayList<>();
        }
    }
    
    private void crearCapaDeSalida(){
        ArrayList<Neurona> capa = new ArrayList<>();
        ArrayList<Double> entradas = new ArrayList<>();
        for (int i = 0; i < red.get(red.size()-1).size(); i++) {
            entradas.add(red.get(red.size()-1).get(i).calcular());
            
        }
        
        for (int i = 0; i < cantidadNeuronasCapaSalida; i++) {
            capa.add(new Neurona((ArrayList<Double>)entradas.clone(), true, false));
        }
        //entradas = new ArrayList<>();
        red.add(capa);
    }
    
    private void propagacionParaAdelante(ArrayList<Double> valoresEntrada){
        ArrayList<Double> entradas = new ArrayList<>();
        
        for (int i = 0; i < red.get(0).size(); i++) {
            entradas.add(valoresEntrada.get(i));
            red.get(0).get(i).setEntradas(entradas);
            
            entradas = new ArrayList<>();
        }
        
        for (int i = 1; i < red.size(); i++) {
            for (int j = 0; j < red.get(i - 1).size(); j++) {
                entradas.add(red.get(i-1).get(j).calcular());
            }
            
            for (int j = 0; j < red.get(i).size(); j++) {
                red.get(i).get(j).setEntradas((ArrayList<Double>)entradas.clone());
            }
            entradas = new ArrayList<>();
        }
    }
    
    public ArrayList<Double> getSalidas(){
        ArrayList<Double> salidas = new ArrayList<>();
        for (int i = 0; i < red.get(red.size()-1).size(); i++) {
            salidas.add(red.get(red.size()-1).get(i).calcular());
        }
        return salidas;
    }
    
    public ArrayList<Double> erroresCapaSalida(ArrayList<Double> salidas,ArrayList<Double> salidasEsperadas){
        ArrayList<Double> errores = new ArrayList<>();
        
        for (int i = 0; i < salidas.size(); i++) {
            errores.add(salidasEsperadas.get(i) - salidas.get(i));
        }
        
        return errores;
    }
    
    public ArrayList<ArrayList<Double>> erroresCapasOcultas(ArrayList<Double> erroresCapaSalida){
        ArrayList<ArrayList<Double>> errores = new ArrayList<>();
        errores.add(erroresCapaSalida);
        ArrayList<Double> erroresDeCapa = new ArrayList<>();
        double sumatoria = 0;
        for(int i = red.size()-2 ; i > 0 ; i--){
            for (int j = 0; j < red.get(i).size(); j++) {
                sumatoria = 0;
                for (int k = 0; k < red.get(i+1).size() ; k++) {
                    sumatoria += erroresCapaSalida.get(k) * red.get(i+1).get(k).getPesos().get(j);
                }
                erroresDeCapa.add(sumatoria);
            }
            errores.add(erroresDeCapa);
            erroresCapaSalida = erroresDeCapa;
            erroresDeCapa=new ArrayList<>();
        }
        
        Collections.reverse(errores);
        
        return errores;
    }
    
    public void modificarPesos(ArrayList<ArrayList<Double>> errores){
        double factorAprendizaje = 0.01;
        ArrayList<Double> pesos= new ArrayList<>();
        ArrayList<Double> entradasNeurona = new ArrayList<>();
        for (int i = 1; i < red.size() ; i++) {  
            for (int j = 0; j < red.get(i).size(); j++) {
                pesos = red.get(i).get(j).getPesos();
                entradasNeurona = red.get(i).get(j).getEntradas(); 
                
                for (int k = 0; k < pesos.size() ; k++) {
                     double delta = factorAprendizaje * errores.get(i-1).get(j) * entradasNeurona.get(k);
                     pesos.set(k,pesos.get(k) + delta);
                }
            }
        }
    }
    
    public void propagacionParaAtras(ArrayList<Double> salidasEsperadas){
        modificarPesos(erroresCapasOcultas(erroresCapaSalida(getSalidas(), salidasEsperadas)));
    }
    
    public void inicializar(ArrayList<Double> entradas ,int neurosasPorCapaOculta[],int neuronasCapaSalida){
        crearCapaEntrada(entradas);
        cantidadCapasOcultas = neurosasPorCapaOculta.length;
        this.cantidadNeuronasCapa = neurosasPorCapaOculta;
        cantidadNeuronasCapaSalida = neuronasCapaSalida;
        crearCapasOcultas();
        crearCapaDeSalida();
    }
    
    public ArrayList<ArrayList<String>> leerEntradas(String csvFile){
        BufferedReader br = null;
        String line = "";
        ArrayList<ArrayList<String>> entradas = new ArrayList<>();
        ArrayList<String> entrada;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) { 
                entrada = new ArrayList<>();
                String[] datos = line.split(",");
                entrada.addAll(Arrays.asList(datos));
                entradas.add(entrada);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return entradas;
    }
    
    private ArrayList<Double> covertirADouble(ArrayList<String> valores){
        ArrayList<Double> entrada = new ArrayList<>();
        for (int i = 0; i < valores.size()-1; i++) {
            entrada.add(Double.parseDouble(valores.get(i)));
        }
        return entrada;
    }
    private boolean sonCorrectas(ArrayList<Double> salidasEsperadas){
        ArrayList<Double> salidas = getSalidas();
        for (int i = 0; i < salidas.size(); i++) {
            if(!Objects.equals(salidas.get(i), salidasEsperadas.get(i))){
                return false;
            }
        }
        
        return true;
    }
    
    private ArrayList<Double> convertirSalidaEsperada(String salida){
        ArrayList<Double> salidaEsperada = null;
        
        switch (salida) {
            case "unacc":
                salidaEsperada = new ArrayList<>(Arrays.asList(0.0,1.0,1.0));
                break;
            case "acc":
                salidaEsperada = new ArrayList<>(Arrays.asList(1.0,0.0,1.0));
                break;
            case "good":
                salidaEsperada = new ArrayList<>(Arrays.asList(1.0,1.0,0.0));
                break;
            case "vgood":
                salidaEsperada = new ArrayList<>(Arrays.asList(1.0,1.0,1.0));
                break;
                
            default:
                break;
        }
        
        return salidaEsperada;
    }
    
    public ArrayList<ArrayList<ArrayList<String>>>  separarDatos(ArrayList<ArrayList<String>> datos, double entrenamiento){
       int cantidad = (int) (entrenamiento * datos.size()) / 100;
       ArrayList<ArrayList<String>> entrenar = new ArrayList<>(datos.subList(0,cantidad));
       ArrayList<ArrayList<String>> prueba = new ArrayList<>(datos.subList(cantidad+1,datos.size()));
       ArrayList<ArrayList<ArrayList<String>>> salida = new ArrayList<>();
       salida.add(entrenar);
       salida.add(prueba);
       return salida;
    }
    
    public void entrenar(){
        ArrayList<ArrayList<String>> datos = leerEntradas("src\\datos\\datos.csv");
        ArrayList<Double> entradas = new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0));
        inicializar(entradas,new int[]{5,5,5},3);
        int epocas = 10000000;
        double errores = 0, porcentajeDeError = 0;
        ArrayList<ArrayList<ArrayList<String>>> nuevos = separarDatos(datos, 70);
        datos = nuevos.get(0);
        for (int i = 0; i < epocas; i++) {
            errores = 0;
            //Collections.shuffle(datos);
            
            for (ArrayList<String> dato : datos) {
                propagacionParaAdelante(covertirADouble(dato));
                ArrayList<Double> salidaEsperada = convertirSalidaEsperada(dato.get(dato.size()-1));
                if(!sonCorrectas(salidaEsperada)){
                    errores++;
                }
                propagacionParaAtras(salidaEsperada);
            }
            porcentajeDeError = (errores * 100) / datos.size();
            System.out.println(porcentajeDeError);
            if(porcentajeDeError<2.3){
                break;
            }
        }
    }
    
    public String getTraducirResultado(ArrayList<Double> resultado){
        
        if(resultado.get(0) == 0 && resultado.get(1) == 1 && resultado.get(2) == 1){
            return "unacc";
        }
        
        if(resultado.get(0) == 1 && resultado.get(1) == 0 && resultado.get(2) == 1){
            return "acc";
        }
        
        if(resultado.get(0) == 1 && resultado.get(1) == 1 && resultado.get(2) == 0){
            return "good";
        }
        
        if(resultado.get(0) == 1 && resultado.get(1) == 1 && resultado.get(2) == 1){
            return "vgood";
        }
        
       return "Desconocida";
    }
    
    public void evaluar(){
        ArrayList<ArrayList<String>> datos = leerEntradas("src\\datos\\datitos.csv");
        Collections.shuffle(datos);
        for (int i = 0; i < datos.size(); i++) {
            propagacionParaAdelante(covertirADouble(datos.get(i)));
            System.out.print(datos.get(i).get(datos.get(i).size()-1)+" -- ");
            System.out.print(getTraducirResultado(getSalidas())+"\n");
            
        }
            
          
    }
}

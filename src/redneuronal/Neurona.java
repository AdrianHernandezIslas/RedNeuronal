/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redneuronal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Adrian
 */
public class Neurona implements Serializable{
    private ArrayList<Double> pesos;
    private ArrayList<Double> entradas;
    private final boolean esEscalonada;
    private final boolean esCapaEntrada;

    public Neurona(ArrayList<Double> entradas, boolean esEscalonada, boolean esEntrada) {
        this.entradas = entradas;
        this.esEscalonada = esEscalonada;
        this.esCapaEntrada = esEntrada;
        this.entradas.add(-1.0);
        generarPesos(this.entradas.size());
    }
    
    private void generarPesos(int cantidadPesos){
        pesos = new ArrayList<>();
        for (int i = 0; i < cantidadPesos ; i++) {
            pesos.add(Math.random());
        }
    }
    
    public double productoPunto(){
        double resultado = 0;
        for (int i = 0; i < pesos.size(); i++) {
            resultado +=  entradas.get(i) * pesos.get(i);    
        }
        return resultado;
    }
    
    public double funcionEscalon(){
        double resultado = productoPunto();
	if (resultado >= 0) {
            return 1;
	}
	else {
            return 0;
	}
    }
    
    double funcionSigmoide() {
	return 1 / (1 + Math.exp(-productoPunto()));
    }
    
    public double calcular() {
	if (esCapaEntrada) {
            return entradas.get(0);
	}else{

            if (esEscalonada) {
		return funcionEscalon();
            }else {
		return funcionSigmoide();
            }
	}
    }

    public ArrayList<Double> getPesos() {
        return pesos;
    }

    public void setPesos(ArrayList<Double> pesos) {
        this.pesos = pesos;
    }

    public ArrayList<Double> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<Double> entradas) {
        this.entradas = entradas;
        this.entradas.add(-1.0);
    }
}


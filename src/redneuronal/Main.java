/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redneuronal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Adrian
 */
public class Main {
    
    private static void guardarEnDisco(RedNeuronal red){
        try {
            FileOutputStream fos = new FileOutputStream(new File("src\\datos\\redNeuronal.obj"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(red);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getCause());
        } catch (IOException ex) {
            System.err.println(ex.getCause());
        }
    }
    
    private static RedNeuronal recuperarRedNeuronal(){
        try {
            FileInputStream fis = new FileInputStream(new File("src\\datos\\redNeuronal.obj"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (RedNeuronal)ois.readObject();
        } catch (FileNotFoundException ex) {
           
        } catch (IOException | ClassNotFoundException ex) {
        }
        return null;
    }
    
    public static void main(String[] args) {
        RedNeuronal red = new RedNeuronal();
        // red = recuperarRedNeuronal() ;
        //if(red != null){
            red.entrenar();
            //red.evaluar();
        //}
        guardarEnDisco(red);
        
    }
}

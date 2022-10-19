package com.example.tallermapas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class mapa {

    private double id;
    private double longitud;
    private double latitud;
    private double distancia;
    private Date fecha;


    public mapa( double id,double longitud, double latitud, Date fecha, double distancia) {
        this.id = id;
        this.longitud = longitud;
        this.latitud = latitud;
        this.fecha = fecha;
        this.distancia = distancia;

    }
    public mapa(){
    }
    public mapa(double latitud, double longitud){
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitud", getLatitud());
            obj.put("longitud", getLongitud());
            obj.put("date", getFecha());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}

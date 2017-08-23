package es.tfandroid.roombarlauncher;

/**
 * Created by root on 23/08/17.
 */

public class TerminalBean {
    String imei="";
    String imei2="";
    String mac="";
    String mac2="";
    String hotel="";
    String habitacion="";
    String passTethering="";
    String nameTethering="";
    String mbTotales="";
    String email="";
    String passSistema="";
    String nombre="";
    String apellido1="";
    String apellido2="";
    String gpsPosition="";
    String tablet="";
    String urlDestino="";

    public TerminalBean(String imei, String imei2, String mac, String mac2, String hotel, String habitacion, String passTethering, String nameTethering, String mbTotales, String email, String passSistema, String nombre, String apellido1, String apellido2, String gpsPosition, String tablet, String urlDestino) {
        this.imei = imei;
        this.imei2 = imei2;
        this.mac = mac;
        this.mac2 = mac2;
        this.hotel = hotel;
        this.habitacion = habitacion;
        this.passTethering = passTethering;
        this.nameTethering = nameTethering;
        this.mbTotales = mbTotales;
        this.email = email;
        this.passSistema = passSistema;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.gpsPosition = gpsPosition;
        this.tablet = tablet;
        this.urlDestino = urlDestino;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac2) {
        this.mac2 = mac2;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public String getPassTethering() {
        return passTethering;
    }

    public void setPassTethering(String passTethering) {
        this.passTethering = passTethering;
    }

    public String getNameTethering() {
        return nameTethering;
    }

    public void setNameTethering(String nameTethering) {
        this.nameTethering = nameTethering;
    }

    public String getMbTotales() {
        return mbTotales;
    }

    public void setMbTotales(String mbTotales) {
        this.mbTotales = mbTotales;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassSistema() {
        return passSistema;
    }

    public void setPassSistema(String passSistema) {
        this.passSistema = passSistema;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getGpsPosition() {
        return gpsPosition;
    }

    public void setGpsPosition(String gpsPosition) {
        this.gpsPosition = gpsPosition;
    }

    public String getTablet() {
        return tablet;
    }

    public void setTablet(String tablet) {
        this.tablet = tablet;
    }

    public String getUrlDestino() {
        return urlDestino;
    }

    public void setUrlDestino(String urlDestino) {
        this.urlDestino = urlDestino;
    }
}

package es.tfandroid.roombarlauncher;

import org.json.JSONException;
import org.json.JSONObject;

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
    String deviceBP="";
    String vendorBP="";
    String romBP="";
    String romVersionBP="";
    String apkVersionBP="";
    String recoverypathBP="";
    String urlApk="";
    String urlROM="";
    String ssid="";
    String passSsid="";
    String ssidKeyType="";
    boolean logoPersonalizado;
    boolean actualizarLogos;

    public TerminalBean(JSONObject jsonObject) throws JSONException {
        this.imei = jsonObject.getString("imei");
        this.imei2 = jsonObject.getString("imei2");
        this.mac = jsonObject.getString("mac");
        this.mac2 = jsonObject.getString("mac2");
        this.hotel = jsonObject.getString("hotel");
        this.habitacion = jsonObject.getString("habitacion");
        this.passTethering = jsonObject.getString("passTethering");
        this.nameTethering = jsonObject.getString("nameTethering");
        this.mbTotales = jsonObject.getString("mbTotales");
        this.email = jsonObject.getString("email");
        this.passSistema = jsonObject.getString("passSistema");
        this.nombre = jsonObject.getString("nombre");
        this.apellido1 = jsonObject.getString("apellido1");
        this.apellido2 = jsonObject.getString("apellido2");
        this.gpsPosition = "";
        this.tablet = jsonObject.getString("tablet");
        this.urlDestino = "";
        this.deviceBP=jsonObject.getString("device");
        this.vendorBP=jsonObject.getString("vendor");
        this.romBP=jsonObject.getString("rom");
        this.romVersionBP=jsonObject.getString("romversion");
        this.apkVersionBP=jsonObject.getString("apkversion");
        this.recoverypathBP=jsonObject.getString("recoverypath");
        this.urlApk=jsonObject.getString("urlApk");
        this.urlROM=jsonObject.getString("urlROM");
        this.ssid=jsonObject.getString("ssid");
        this.passSsid=jsonObject.getString("passSsid");
        this.ssidKeyType=jsonObject.getString("ssidKeyType");
        this.logoPersonalizado="1".equals(jsonObject.getString("logoPersonalizado"))?true:false;
        this.actualizarLogos="1".equals(jsonObject.getString("actualizarLogos"))?true:false;
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

    public String getDeviceBP() {
        return deviceBP;
    }

    public void setDeviceBP(String deviceBP) {
        this.deviceBP = deviceBP;
    }

    public String getVendorBP() {
        return vendorBP;
    }

    public void setVendorBP(String vendorBP) {
        this.vendorBP = vendorBP;
    }

    public String getRomBP() {
        return romBP;
    }

    public void setRomBP(String romBP) {
        this.romBP = romBP;
    }

    public String getRomVersionBP() {
        return romVersionBP;
    }

    public void setRomVersionBP(String romVersionBP) {
        this.romVersionBP = romVersionBP;
    }

    public String getApkVersionBP() {
        return apkVersionBP;
    }

    public void setApkVersionBP(String apkVersionBP) {
        this.apkVersionBP = apkVersionBP;
    }

    public String getRecoverypathBP() {
        return recoverypathBP;
    }

    public void setRecoverypathBP(String recoverypathBP) {
        this.recoverypathBP = recoverypathBP;
    }

    public String getUrlApk() {
        return urlApk;
    }

    public void setUrlApk(String urlApk) {
        this.urlApk = urlApk;
    }

    public String getUrlROM() {
        return urlROM;
    }

    public void setUrlROM(String urlROM) {
        this.urlROM = urlROM;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassSsid() {
        return passSsid;
    }

    public void setPassSsid(String passSsid) {
        this.passSsid = passSsid;
    }

    public String getSsidKeyType() {
        return ssidKeyType;
    }

    public void setSsidKeyType(String ssidKeyType) {
        this.ssidKeyType = ssidKeyType;
    }

    public boolean getLogoPersonalizado() {
        return logoPersonalizado;
    }

    public void setLogoPersonalizado(boolean logoPersonalizado) {
        this.logoPersonalizado = logoPersonalizado;
    }

    public boolean getActualizarLogos() {
        return actualizarLogos;
    }

    public void setActualizarLogos(boolean actualizarLogos) {
        this.actualizarLogos = actualizarLogos;
    }
}

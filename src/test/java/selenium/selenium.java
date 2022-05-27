package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.zaproxy.clientapi.core.ClientApi;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class selenium {

    private static final String ZAP_PROXYHOST = "localhost";
    private static final int ZAP_PROXYPORT = 8081;//Aqui se ponen el nombre y el numero de puerto del Zap
    private static final String URL= "https://www.instagram.com/";//Se pone la url de la pagina que deseemos escanear
    private static WebDriver driver;//Usamo el webdriver de conexion

    public static void main (String args[])throws Exception{

ClientApi clienteApi = new ClientApi(ZAP_PROXYHOST,ZAP_PROXYPORT);//instancia  que permite hacer la conexion al zap
Proxy seleniumProxy = new Proxy();
seleniumProxy.setProxyAutoconfigUrl("https://"+ZAP_PROXYHOST+":"+ZAP_PROXYPORT);//clase de selenium para la conexion


clienteApi.ascan.removeAllScans();//borrar las ejecuciones que han habido dentro de zap
clienteApi.core.newSession("","");//Crearmos una nueva sesion en zap
clienteApi.spider.scan(URL,null,null,null,null);//Robot que navega por la web y las guarda para posteriormete scanearlas


        WebDriverManager.chromedriver().setup();//Libreria para evitar configuaracion de forma manual, busca la version y descarga los driver de forma automatica
        ChromeOptions chromeOptions= new ChromeOptions();
        chromeOptions.setCapability(CapabilityType.PROXY,seleniumProxy);//Configuracion del proxy
        chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);//para scanera cuando tiene certificados seguro e inseguros
        chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);

//Prueba
        clienteApi.pscan.enableAllScanners();//Habilitas todos los tipos de scanner
        clienteApi.ascan.scan(URL,"true","false",null,null,null);//Se pasa la url y se escanea

        driver = new ChromeDriver(chromeOptions);
        driver.get(URL);//Evalua las paginas que le vamos diciendo
        waitMS(5000);//Espera de 5 segundos para pasar de pagina a pagina
        driver.close();//Cierra la conexion

        String report= new String(clienteApi.core.htmlreport());
        Path filepath = Paths.get(System.getProperty("user.dir")+"/scan-results/seleniumTests.html");//Se crea un reporte y se guarda en esta direccion dentro del ide
        Files.deleteIfExists(filepath);
        Files.write(filepath,report.getBytes());

        clienteApi.ascan.removeAllScans();//Borra los scaners
        clienteApi.core.newSession("","");//Vuelve a crear una sesion

    }
public static void waitMS(int millis) throws Exception{
        Thread.sleep(millis);//Metodo para intenrrupir una ejecucion

}

}

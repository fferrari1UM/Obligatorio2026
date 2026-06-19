package uy.edu.um.doors;

import jdk.jfr.Event;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStackImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ProcessManagerImpl implements ProcessManager{
    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private MyHashImpl<Integer, Usuario> usuarios = new MyHashImpl<>();
    private MyQueueImpl<Proceso> nuevos = new MyQueueImpl<>();
    private MyHeapImpl<Proceso> pendientes = new MyHeapImpl<>();
    private MyStackImpl<Proceso> finalizados = new MyStackImpl<>();
    private Proceso enEjecucion = null;
    private int MAX_FINALIZADOS = 10;

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath){
        try {
            Scanner scannerUs = new Scanner(new File(usersCsvPath));
            scannerUs.nextLine();
            while (scannerUs.hasNextLine()) {
                String linea = scannerUs.nextLine();
                String[] partes = linea.split(";");
                Usuario usuario = new Usuario(Integer.parseInt(partes[0]), partes[1], partes[2]);
                usuarios.put(usuario.getUid(), usuario);
            }
            scannerUs.close();

            Scanner scannerPro = new Scanner(new File(processCsvPath));
            scannerPro.nextLine();
            while (scannerPro.hasNextLine()) {
                String linea = scannerPro.nextLine();
                String[] partes = linea.split(";");
                Usuario propietario = usuarios.get(Integer.parseInt(partes[1]));
                Proceso proceso = new Proceso(Integer.parseInt(partes[0]), partes[2], propietario);
                String eventos = partes[3].replace("{", "").replace("}", "");
                String[] eventosArr = eventos.split("#");
                for (String eventoStr : eventosArr) {
                    eventoStr = eventoStr.trim();
                    String[] parteEvt = eventoStr.split(":");
                    String tipo = parteEvt[0].trim();
                    String instrucciones = parteEvt[1].replace("[", "").replace("]", "");
                    String[] instruccionesArr = instrucciones.split(",\\s+");
                    Evento evento = new Evento(tipo);
                    for (String instruccion : instruccionesArr) {
                        evento.getInstructions().add(instruccion);
                    }
                    proceso.getEventos().add(evento);
                }
                nuevos.enqueue(proceso);
            }
            scannerPro.close();
        } catch (FileNotFoundException e){
            System.out.println("No se encontro el archivo");
        }
    }

    @Override
    public void prepareProcesses() {
        while (!nuevos.isEmpty()){
            Proceso proceso = null;
            try {
                proceso = nuevos.dequeue();
            } catch (EmptyQueueException e){
                break;
            }
            int cantCpu = 0;
            int cantRam = 0;
            int cantDisk = 0;
            for(int i = 0; i < proceso.getEventos().size(); i++){
                Evento evento = proceso.getEventos().get(i);
                if (evento.getType().equals("CPU")){
                    cantCpu++;
                }else if (evento.getType().equals("RAM")) {
                    cantRam++;
                }else {
                    cantDisk++;
                }
            }
            int cantEventos = proceso.getEventos().size();
            int w = proceso.getPropietario().getTipo().equals("ADMIN") ? 32 : 16;
            int prioridad = (8*cantCpu + 2*cantRam + 2*cantDisk)/ cantEventos + w * cantEventos;
            proceso.setPrioridad(prioridad);
            proceso.setEstado("PENDING");
            pendientes.insert(proceso);
            String mensaje = "NEW PENDING PROCESS: PID=" + proceso.getPid() + " | " + proceso.getNombre() + " | USER:" + proceso.getPropietario().getAlias() + " UID:" + proceso.getPropietario().getUid() + " | P=" + proceso.getPrioridad();
            escribirLog(mensaje);
        }

    }

    @Override
    public void executeNextProcess() {
        if (enEjecucion != null){
            System.out.println("Ya se encuentra un proceso en ejecucion");
            return;
        }
        if (pendientes.isEmpty()){
            System.out.println("No hay procesos pendientes");
            return;
        }
        Proceso proceso = null;
        try{
            proceso = pendientes.remove();
        } catch (EmptyHeapException e){
            return;
        }
        proceso.setEstado("RUNNING");
        enEjecucion = proceso;
        String mensaje = "EXECUTING PROCESS: PID=" + proceso.getPid() + " | USER:" + proceso.getPropietario().getAlias() + " UID:" + proceso.getPropietario().getUid();
        for (int i = 0; i < enEjecucion.getEventos().size(); i++){
            Evento evento = enEjecucion.getEventos().get(i);
            String instrucciones = "";
            for (int j = 0; j < evento.getInstructions().size(); j++){
                instrucciones += evento.getInstructions().get(j);
                if (j < evento.getInstructions().size() -1) {
                    instrucciones += ", ";
                }
            }
            mensaje += "\n EVENT: " + evento.getType() + " | Instructions [" + instrucciones + "]";
        }
        escribirLog(mensaje);
    }

    @Override
    public void finishProcessOk() {
        if (enEjecucion == null){
            System.out.println("No se encuentra ningun proceso en ejecucion");
            return;
        }
        enEjecucion.setEstado("FINISHED");
        enEjecucion.setTipoFinalizacion("OK");
        if (finalizados.size() == MAX_FINALIZADOS){
            String submensaje = "Finished process stack overflow";
            while (!finalizados.isEmpty()){
                Proceso proceso = null;
                try {
                    proceso = finalizados.pop();
                }catch (EmptyStackException e){
                    break;
                }
                submensaje += "\nPID=" + proceso.getPid() + " " + proceso.getNombre() + " | STATE " + proceso.getTipoFinalizacion() + " | USER:" + proceso.getPropietario().getAlias() + " UID:" + proceso.getPropietario().getUid();
            }
            escribirLog(submensaje);
        }
        finalizados.push(enEjecucion);
        String mensaje = "ENDING PROCESS: PID=" + enEjecucion.getPid() + " | STATE: OK";
        escribirLog(mensaje);
        enEjecucion = null;
    }

    @Override
    public void finishProcessError() {
        if (enEjecucion == null){
            System.out.println("No se encuentra ningun proceso en ejecucion");
            return;
        }
        enEjecucion.setEstado("FINISHED");
        enEjecucion.setTipoFinalizacion("ERROR");
        if (finalizados.size() == MAX_FINALIZADOS){
            String submensaje = "Finished process stack overflow";
            while (!finalizados.isEmpty()){
                Proceso proceso = null;
                try {
                    proceso = finalizados.pop();
                }catch (EmptyStackException e){
                    break;
                }
                submensaje += "\nPID=" + proceso.getPid() + " " + proceso.getNombre() + " | STATE: " + proceso.getTipoFinalizacion() + " | USER:" + proceso.getPropietario().getAlias() + " UID:" + proceso.getPropietario().getUid();
            }
            escribirLog(submensaje);
        }
        finalizados.push(enEjecucion);
        String mensaje = "ENDING PROCESS: PID=" + enEjecucion.getPid() + " | STATE: ERROR";
        escribirLog(mensaje);
        enEjecucion = null;
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {

        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {


        System.out.println("PROCESS STATUS VERBOSE");

        System.out.println("EXECUTING:");
        if (enEjecucion != null) {
            System.out.println("\tPID=" + enEjecucion.getPid() + " | " + enEjecucion.getNombre()
                    + " | USER:" + enEjecucion.getPropietario().getAlias()
                    + " UID:" + enEjecucion.getPropietario().getUid()
                    + " | P=" + enEjecucion.getPrioridad());
            printEventos(enEjecucion);
        }

        System.out.println("PENDING:");
        MyLinkedListImpl<Proceso> tempPendientes = new MyLinkedListImpl<>();
        while (!pendientes.isEmpty()) {
            try {
                Proceso p = pendientes.remove();
                System.out.println("\tPID=" + p.getPid() + " | " + p.getNombre()
                        + " | USER:" + p.getPropietario().getAlias()
                        + " UID:" + p.getPropietario().getUid()
                        + " | P=" + p.getPrioridad());
                printEventos(p);
                tempPendientes.add(p);
            } catch (EmptyHeapException e) {
                break;
            }
        }

        for (int i = 0; i < tempPendientes.size(); i++) {
            pendientes.insert(tempPendientes.get(i));
        }

        System.out.println("FINISHED:");
        MyLinkedListImpl<Proceso> tempFinalizados = new MyLinkedListImpl<>();
        while (!finalizados.isEmpty()) {
            try {
                Proceso p = finalizados.pop();
                tempFinalizados.add(p);
            } catch (EmptyStackException e) {
                break;
            }
        }

        for (int i = 0; i < tempFinalizados.size(); i++) {
            Proceso p = tempFinalizados.get(i);
            System.out.println("\tPID=" + p.getPid() + " " + p.getNombre()
                    + " | STATE: " + p.getTipoFinalizacion()
                    + " | USER:" + p.getPropietario().getAlias()
                    + " UID:" + p.getPropietario().getUid());
            printEventos(p);
            finalizados.push(p);
        }
    }

    private void printEventos(Proceso p) {
        for (int i = 0; i < p.getEventos().size(); i++) {
            Evento e = p.getEventos().get(i);
            String instrucciones = "";
            for (int j = 0; j < e.getInstructions().size(); j++) {
                instrucciones += e.getInstructions().get(j);
                if (j < e.getInstructions().size() - 1) instrucciones += ", ";
            }
            System.out.println("\t\tEVENT: " + e.getType() + " | Instructions [" + instrucciones + "]");
        }

    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }

    public void escribirLog(String mensaje){
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = formato.format(new Date());
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format(new Date());
            String archivo = "LOGS_PROCESS" + fecha;
            FileWriter fw = new FileWriter(archivo, true);
            fw.write("[" + timestamp + "]: " + mensaje + "\n");
            fw.close();
        } catch (IOException e){
            System.out.println("ERROR, no se pudo escribir el log");
        }
    }
}

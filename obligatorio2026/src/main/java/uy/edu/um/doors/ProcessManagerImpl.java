package uy.edu.um.doors;

import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.MyStackImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProcessManagerImpl implements ProcessManager{
    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private MyHashImpl<Integer, Usuario> usuarios;
    private MyQueueImpl<Proceso> nuevos;
    private MyHeapImpl<Proceso> pendientes;
    private MyStackImpl<Proceso> finalizados;
    private Proceso enEjecucion = null;

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
                    String[] instruccionesArr = instrucciones.split(" ");
                    Evento evento = new Evento(tipo);
                    for (String instruccion : instruccionesArr) {
                        evento.getInstructions().add(instruccion);
                    }
                    proceso.getEventos().add(evento);
                }
                nuevos.enqueue(proceso);
                System.out.println("IMPLEMENTAR");
            }
            scannerPro.close();
        } catch (FileNotFoundException e){
            System.out.println("No se encontro el archivo");
        }
    }

    @Override
    public void prepareProcesses() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void executeNextProcess() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
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
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}

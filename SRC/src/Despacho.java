import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho extends Thread {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random Random = new Random();


    public Despacho(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    @Override
    public void run() {
        while (true) {
            try{
                synchronized (gestor.getMonitorDespacho()){
                    gestor.getMonitorDespacho().wait();
                }
                if (gestor.addDespachado() >= 500) {
                    break; //Una vez que se prepararon 500 pedidos termina el hilo
                }
                int[] pos = buscarCasilleroOcupado();
                if (pos == null) {
                    DormirHilo();
                    continue; // no hay casilleros ocupados, vuelvo a intentar
                }

                    int i = pos[0];
                    int j = pos[1];

                boolean verificacionExitosa = Random.nextInt(100) <= 85; // 85% de probabilidad

                synchronized (gestor.getAlmacen()[i][j]) {
                    if (verificacionExitosa) {
                        gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.VACIO);
                        gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                        gestor.modificarRegistro(gestor.getPedEnTran(), "AGREGAR");
                        gestor.aumentarContador();
                        synchronized (gestor.getMonitorEntrega()){
                            gestor.getMonitorEntrega().notifyAll();
                        }

                    } else {
                        gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                        gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                        gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                    }
                }
                int total = gestor.addDespachado();
                if (total >= 500) {break;}
                DormirHilo();


            } catch (Exception e) {
                Thread.currentThread().interrupt();                                     //Si se da la excepcion salgo del bucle
                break;
            }
        }
    }

    private void DormirHilo() {
        try {
            int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Busca un casillero que esté ocupado para simular que tiene un pedido en preparación
    private int[] buscarCasilleroOcupado() {
        if(gestor.getPedEnPrep().getContador() == 0){
            return null;// no encontró ninguno
        }
        Casillero[][] almacen = gestor.getAlmacen();    //Obtenemos el almacen
        int[] pos = gestor.randomPos();
        while (almacen[pos[0]][pos[1]].getEstado() != Estado_Casilleros.OCUPADO) {
                pos = gestor.randomPos();
            }
            return pos;


//        else{
//            for (int i=0; i<almacen.length; i++) {
//                for (int j=0; j<almacen[i].length; j++) {
//                    if (almacen[i][j].getEstado() == Estado_Casilleros.OCUPADO) {   //Pregunto si esta ocupado
//                        return new int[]{i, j};                     //si esta ocupado busco un nuevo lugar en la matriz
//                    }
//                }
//            }
//        }
//        return null;


    }
}

import java.util.concurrent.atomic.AtomicInteger;

public class Casillero {
    private Estado_Casilleros estado;
    private AtomicInteger contador;

    public Casillero(){
        estado = Estado_Casilleros.VACIO;
        contador = new AtomicInteger(0);
    }

    public void aumentarContador(){
        contador.incrementAndGet();
    }

    public int getContador(){
        return contador.get();
    }

    public void cambiarEstado(Estado_Casilleros estado){
        this.estado= estado;
    }

    public Estado_Casilleros getEstado() {
        return estado;
    }




}

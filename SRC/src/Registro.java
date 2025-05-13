import java.util.concurrent.atomic.AtomicInteger;

public class Registro {
    private AtomicInteger contador;
    private final Estado_Pedidos tipoPedido;


    public Registro(Estado_Pedidos pedido){
        contador = new AtomicInteger(0);
        tipoPedido = pedido;
    }

    public void agregarPedido(){
        contador.incrementAndGet();
    }

    public void eliminarPedido(){
        contador.decrementAndGet();
    }


    public int getContador(){
        return contador.get();
    }

    public Estado_Pedidos getTipoPedido(){
        return tipoPedido;
    }


}

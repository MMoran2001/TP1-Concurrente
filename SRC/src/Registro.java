import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Registro{
    private final AtomicInteger contador;
    private final Estado_Pedidos tipoPedido;

    private final List<Pedido> pedidos;


    public Registro(Estado_Pedidos pedido) {
        contador = new AtomicInteger(0);
        tipoPedido = pedido;
        pedidos = new ArrayList<>();
    }
    public void agregarPedido(Pedido pedido) {
        if (pedido.getTipoPedido() == tipoPedido) {
            pedidos.add(pedido);
            contador.incrementAndGet();
        }
    }
    public synchronized void eliminarPedido() {
        int id = getIndicePedido();
        if (id != -1 && pedidos.get(id)!=null) {
            pedidos.remove(id);
            contador.decrementAndGet();}
    }

public synchronized int getContador() {
    return contador.get();
}

public Estado_Pedidos getTipoPedido() {
    return tipoPedido;
}
public List<Pedido> getListaPedidos() {
    return pedidos;
}
public int getIndicePedido() {
    Random random = new Random();
    if(pedidos.size()>1) {
        return random.nextInt(pedidos.size());
    }else if(pedidos.size()==1) {
        return 0;
    }
    return -1;
}

}
public class Casillero {
    private Estado_Casilleros estado;
    private int contador;

    public Casillero(){
        estado = Estado_Casilleros.VACIO;
        contador = 0;
    }

    public void aumentarContador(){
        contador++;
    }

    public int getContador(){
        return contador;
    }

    public Estado_Casilleros cambiarEstado(Estado_Casilleros estado){
        estado = this.estado;
        return estado;
    }

    public Estado_Casilleros getEstado() {
        return estado;
    }




}

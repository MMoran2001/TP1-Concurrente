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

    public void cambiarEstado(Estado_Casilleros estado){
        this.estado= estado;
    }

    public Estado_Casilleros getEstado() {
        return estado;
    }




}


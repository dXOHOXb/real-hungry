package Modules;
import java.awt.*;


interface UIEssents {
    public Font defaultFont();
    public Color defaultTXTcolor();
}

final public class UIConstants implements UIEssents{
    public Font defaultFont(){
        return new Font("Comic Sans MS",Font.BOLD,24);
    }
    public Font defaultButtonsFont(){
        return new Font("Comic Sans MS",Font.BOLD,36);
    }
    public Color defaultTXTcolor(){
        return new Color(36,36,36);
    }
}


package abstract_factory_pattern;

interface Window{
    void createWindow();
    void closeWindow();
}

interface Scrollbar{
    void createScrollbar();
    void scroll();
}

interface Dialog{
    void createDialog();
    void showDialog();
}

class WindowsWindow implements Window{
    @Override
    public void createWindow() {
        System.out.println("Creating Windows Window");
    }

    @Override
    public void closeWindow() {
        System.out.println("Closing Windows Window");
    }
}

class WindowsScrollbar implements Scrollbar{
    @Override
    public void createScrollbar() {
        System.out.println("Creating Windows Scrollbar");
    }

    @Override
    public void scroll() {
        System.out.println("Scrolling Windows Scrollbar");
    }
}

class WindowsDialog implements Dialog{
    @Override
    public void createDialog() {
        System.out.println("Creating Windows Dialog");
    }

    @Override
    public void showDialog() {
        System.out.println("Showing Windows Dialog");
    }
}

class MacOSWindow implements Window{
    @Override
    public void createWindow() {
        System.out.println("Creating MacOS Window");
    }

    @Override
    public void closeWindow() {
        System.out.println("Closing MacOS Window");
    }
}

class MacOSScrollbar implements Scrollbar{
    @Override
    public void createScrollbar() {
        System.out.println("Creating MacOS Scrollbar");
    }

    @Override
    public void scroll() {
        System.out.println("Scrolling MacOS Scrollbar");
    }
}

class MacOSDialog implements Dialog{
    @Override
    public void createDialog() {
        System.out.println("Creating MacOS Dialog");
    }

    @Override
    public void showDialog() {
        System.out.println("Showing MacOS Dialog");
    }
}





interface  OSFactory{
    Window getWindow();
   Scrollbar getScrollbar();
     Dialog getDialog();
}

class WindowsFactory implements OSFactory{
    @Override
    public Window getWindow() {
        return new WindowsWindow();
    }

    @Override
    public Scrollbar getScrollbar() {
        return new WindowsScrollbar();
    }

    @Override
    public Dialog getDialog() {
        return new WindowsDialog();
    }
}


class MacOSFactory implements OSFactory{
    @Override
    public Window getWindow() {
        return new MacOSWindow();
    }

    @Override
    public Scrollbar getScrollbar() {
        return new MacOSScrollbar();
    }

    @Override
    public Dialog getDialog() {
        return new MacOSDialog();
    }
}


public class CrossPlatformGUI {

    public OSFactory getFactory(String osType){
        if(osType.equalsIgnoreCase("Windows")){
            return new WindowsFactory();
        } else if(osType.equalsIgnoreCase("MacOS")){
            return new MacOSFactory();
        }
        return null;
    }

    public static void main(String[] args) {
        CrossPlatformGUI gui = new CrossPlatformGUI();
        OSFactory factory = gui.getFactory("Windows");

        Window window = factory.getWindow();
        Scrollbar scrollbar = factory.getScrollbar();
        Dialog dialog = factory.getDialog();

        window.createWindow();
        scrollbar.createScrollbar();
        dialog.createDialog();

        window.closeWindow();
        scrollbar.scroll();
        dialog.showDialog();
    }

}

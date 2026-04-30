package abstract_factory_pattern;

interface Button{
    void render();
    void onClick();
}

interface Checkbox{
    void render();
    void onSelect();
}

abstract class UIFactory
{
    public abstract Button createButton();
    public abstract Checkbox createCheckbox();
}

class LightButton implements Button
{
    @Override
    public void render() {
        System.out.println("Rendering Light Button");
    }

    @Override
    public void onClick() {
        System.out.println("Light Button Clicked");
    }
}

class DarkButton implements Button
{
    @Override
    public void render() {
        System.out.println("Rendering Dark Button");
    }

    @Override
    public void onClick() {
        System.out.println("Dark Button Clicked");
    }
}

class LightCheckbox implements Checkbox
{
    @Override
    public void render() {
        System.out.println("Rendering Light Checkbox");
    }

    @Override
    public void onSelect() {
        System.out.println("Light Checkbox Selected");
    }
}

class DarkCheckbox implements Checkbox
{
    @Override
    public void render() {
        System.out.println("Rendering Dark Checkbox");
    }

    @Override
    public void onSelect() {
        System.out.println("Dark Checkbox Selected");
    }
}

class LightThemeFactory extends UIFactory
{
    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new LightCheckbox();
    }
}

class DarkThemeFactory extends UIFactory
{
    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new DarkCheckbox();
    }
}



public class UIThemeSetter 
{
    private UIFactory getFactory(String theme) {
        if (theme.equalsIgnoreCase("light")) {
            return new LightThemeFactory();
        } else if (theme.equalsIgnoreCase("dark")) {
            return new DarkThemeFactory();
        }
        throw new IllegalArgumentException("Unknown theme: " + theme);
    }
    public static void main(String[] args) {
        UIThemeSetter themeSetter = new UIThemeSetter();
        UIFactory factory = themeSetter.getFactory("light");

        Button button = factory.createButton();
        Checkbox checkbox = factory.createCheckbox();

        button.render();
        button.onClick();
        checkbox.render();
        checkbox.onSelect();
    }

}

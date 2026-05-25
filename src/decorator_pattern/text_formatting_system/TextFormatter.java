package decorator_pattern.text_formatting_system;

interface Text {
    String render();
}
// The document editor could be bold, italic, underlined or  

class PlainText implements Text {
    private String content;

    public PlainText(String content) {
        this.content = content;
    }

    @Override
    public String render() {
        return content;
    }
}

// decorator 
class BaseDecorator implements Text {
    protected Text text;

    public BaseDecorator(Text text) {
        this.text = text;
    }

    @Override
    public String render() {
        return text.render();
    }
}

class BoldDecorator extends BaseDecorator {
    public BoldDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<b>" + super.render() + "</b>";
    }
}


class ItalicDecorator extends BaseDecorator {
    public ItalicDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<i>" + super.render() + "</i>";
    }
}

class UnderlineDecorator extends BaseDecorator {
    public UnderlineDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<u>" + super.render() + "</u>";
    }
}

public class TextFormatter {
    public static void main(String[] args) {
        Text myText = new PlainText("Hello, World!");
        System.out.println(myText.render());

        Text boldText = new BoldDecorator(myText);
        System.out.println(boldText.render());

        Text italicText = new ItalicDecorator(boldText);
        System.out.println(italicText.render());

        Text underlinedText = new UnderlineDecorator(italicText);
        System.out.println(underlinedText.render());
    }

}

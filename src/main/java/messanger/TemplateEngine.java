package messanger;

public interface TemplateEngine {
    String prepareMessage(Template template, Client client);
}

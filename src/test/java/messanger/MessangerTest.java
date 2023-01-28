package messanger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.Before;

public class MessangerTest {

    private MailServer mailServer;
    private TemplateEngine templateEngine;
    private Messenger messenger;
    private Client client;
    private Template template;

    @Before
    public void setUp() {
        mailServer = mock(MailServer.class);
        templateEngine = mock(TemplateEngine.class);
        messenger = new Messenger(mailServer, templateEngine);
        client = mock(Client.class);
        template = mock(Template.class);
    }

    @Test
    public void testSendMessage() {

        when(templateEngine.prepareMessage(template, client)).thenReturn("test message");
        messenger.sendMessage(client, template);

        verify(mailServer).send(eq(client.getEmail()), eq("test message"));
    }

    @Test(expected = NullPointerException.class)
    public void sendMessageClientNullTest() {

        TemplateEngine templateEngineMock = mock(TemplateEngine.class);
        MailServer mailServerMock = mock(MailServer.class);
        Messenger messenger = new Messenger(mailServerMock, templateEngineMock);
        Template template = mock(Template.class);
        messenger.sendMessage(null, template);
    }

    @Test
    public void testSendMessageVerifyNoMoreInteractions() {
        when(templateEngine.prepareMessage(template, client)).thenReturn("test message");
        messenger.sendMessage(client, template);
        verify(mailServer).send(client.getEmail(), "test message");
        verifyNoMoreInteractions(mailServer);
    }

    @Test
    public void testSendMessageWithTimes() {
        when(templateEngine.prepareMessage(template, client)).thenReturn("test message");
        messenger.sendMessage(client, template);
        messenger.sendMessage(client, template);
        verify(mailServer, times(2)).send(client.getEmail(), "test message");
    }

    @Test
    public void testSendMessageWithAtLeastOnce() {
        when(templateEngine.prepareMessage(template, client)).thenReturn("test message");
        messenger.sendMessage(client, template);
        verify(mailServer, atLeastOnce()).send(client.getEmail(), "test message");
    }

    @Test
    public void testSendMessageAgain() {
        String preparedMessage = "Hello, John";
        when(templateEngine.prepareMessage(template, client)).thenReturn(preparedMessage);
        messenger.sendMessage(client, template);
        verify(mailServer).send(client.getEmail(), preparedMessage);
    }

    @Test
    public void testSendMessageWithDifferentTemplate() {
        String newTemplate = "Welcome, ${name}";
        template.setTemplate(newTemplate);
        String preparedMessage = "Welcome, John";
        when(templateEngine.prepareMessage(template, client)).thenReturn(preparedMessage);
        messenger.sendMessage(client, template);
        verify(mailServer).send(client.getEmail(), preparedMessage);
    }

    @Test
    public void testSendMessageWithNullTemplate() {
        template = null;
        messenger.sendMessage(client, template);
        verify(templateEngine, never()).prepareMessage(any(Template.class), any(Client.class));
        verify(mailServer, never()).send(anyString(), anyString());
    }

    @Test
    public void testSendMessageAnother() {
        when(client.getEmail()).thenReturn("test@example.com");
        when(templateEngine.prepareMessage(template, client)).thenReturn("Test message content");

        Messenger messenger = new Messenger(mailServer, templateEngine);
        messenger.sendMessage(client, template);
        org.mockito.Mockito.verify(mailServer).send("test@example.com", "Test message content");
    }

    @Test
    public void testGetEmail() {
        when(client.getEmail()).thenReturn("test@example.com");
        assertEquals("test@example.com", client.getEmail());
    }

    @Test
    public void testGetTemplate() {
        when(template.getTemplate()).thenReturn("Test template");
        assertEquals("Test template", template.getTemplate());
    }


}
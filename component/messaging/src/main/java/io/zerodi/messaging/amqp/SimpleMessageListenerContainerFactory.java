package io.zerodi.messaging.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import io.zerodi.messaging.configuration.MessagingConfiguration;

@Component
public class SimpleMessageListenerContainerFactory {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MessagingConfiguration messagingConfiguration;

    public SimpleMessageListenerContainer createMessageListenerContainer(MessageListenerAdapter messageListener, Queue... queuesToListenOn) {
        validateInput(messageListener, queuesToListenOn);

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(queuesToListenOn);
        simpleMessageListenerContainer.setMessageListener(messageListener);
        simpleMessageListenerContainer.setConcurrentConsumers(messagingConfiguration.getConcurrentConsumers());

        return simpleMessageListenerContainer;
    }

    private void validateInput(final Object messageListener, final Queue[] queuesToListenOn) {
        Preconditions.checkNotNull(queuesToListenOn, "queuesToListenOn cannot be null!");
        Preconditions.checkNotNull(messageListener, "messageListener cannot be null!");
    }
}

package com.company.candyshop.screen.order;

import com.company.candyshop.app.OrderService;
import io.jmix.core.Messages;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.screen.*;
import com.company.candyshop.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Order_.edit")
@UiDescriptor("order-edit.xml")
@EditedEntityContainer("orderDc")
public class OrderEdit extends StandardEditor<Order> {
    @Autowired
    private OrderService orderService;
    @Autowired
    private Notifications notifications;
    @Autowired
    private Messages messages;
    @Subscribe
    public void onInitEntity(InitEntityEvent<Order> event) {
        event.getEntity().setOrderNumber(orderService.getNewOrderNumber());
        if (event.getEntity().getServeReady() == null)
            event.getEntity().setServeReady(false);
    }
    @Subscribe("serveCheck")
    public void onServeCheck(Action.ActionPerformedEvent event) {
        Order order = getEditedEntity();
        if (orderService.checkHaveConfectioneries(order) && (!order.getServeReady())) {
            orderService.consumeConfectioneries(order);
            order.setServeReady(true);
        } else notifications.create().withCaption(messages.getMessage("msg://localization/denyOrder.serving"))
                .withType(Notifications.NotificationType.HUMANIZED)
                .show();
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Order order = getEditedEntity();
        order.setStartReady(orderService.checkHaveResources(order));
    }
}
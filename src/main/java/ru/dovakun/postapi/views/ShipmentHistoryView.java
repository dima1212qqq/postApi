package ru.dovakun.postapi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;
import ru.dovakun.postapi.repo.ShipmentRepo;


@Route(value = "shipment_history", layout = MainLayout.class)
public class ShipmentHistoryView extends Div {
    public ShipmentHistoryView(@Autowired ShipmentRepo shipmentService, @Autowired ShipmentHistoryRepo shipmentHistoryRepo) {
        IntegerField findByIdShipment = new IntegerField("Строка для поиска");
        Grid<ShipmentHistory> grid = new Grid<>();
        grid.addColumn(ShipmentHistory::getShipmentId).setHeader("Какое отправление");
        grid.addColumn(ShipmentHistory::getStatus).setHeader("Статус");
        grid.addColumn(ShipmentHistory::getCurrentPostOffice).setHeader("Где письмо");
        grid.addColumn(ShipmentHistory::getTimestamp).setHeader("Время прибытие");
        Button show = new Button("Показать", buttonClickEvent -> {
            grid.setItems(shipmentHistoryRepo.findByShipmentId(findByIdShipment.getValue().longValue()));
        });
        add(findByIdShipment,show,grid);
    }

}

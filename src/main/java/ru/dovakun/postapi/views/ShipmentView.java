package ru.dovakun.postapi.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.enums.TypeShipment;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.repo.PostOfficeRepo;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;
import ru.dovakun.postapi.service.PostOfficeService;
import ru.dovakun.postapi.service.ShipmentService;

import java.time.LocalDateTime;

@Route(value = "shipment", layout = MainLayout.class)
public class ShipmentView extends Div {
    private final PostOfficeRepo postOfficeRepo;
    private final ShipmentHistoryRepo shipmentHistoryRepo;
    private Grid<Shipment> grid;
    private Shipment shipment;
    private Dialog dialog = new Dialog();
    private TextField name;
    private ComboBox<TypeShipment> type;
    private TextField index;
    private TextField address;
    private ComboBox<PostOffice> originPostOffice;
    private ComboBox<PostOffice> destinationPostOffice;
    private Button saveOnDialog;
    private Button cancelOnDialog;
    private VerticalLayout dialogLayout;

    public ShipmentView(@Autowired ShipmentService shipmentService, PostOfficeRepo postOfficeRepo, @Autowired ShipmentHistoryRepo shipmentHistoryRepo) {
        HorizontalLayout gridLayout = new HorizontalLayout();
        this.grid = new Grid<>();
        this.grid.addColumn(Shipment::getName).setHeader("Название посылки");
        this.grid.addColumn(Shipment::getType).setHeader("Тип посылки");
        this.grid.addColumn(Shipment::getAddress).setHeader("Адрес получателя");
        this.grid.addColumn(Shipment::getIndex).setHeader("Индекс получателя");
        this.grid.addColumn(Shipment::getStatus).setHeader("Статус посылки");
        this.grid.addColumn(Shipment::getOriginPostOffice).setHeader("Место отправки");
        this.grid.addColumn(Shipment::getCurrentPostOffice).setHeader("Место нахождения");
        this.grid.addColumn(Shipment::getDestinationPostOffice).setHeader("Место доставки");
        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.grid.setItemDetailsRenderer(new ComponentRenderer<>(shipment -> new HorizontalLayout(
                new Button("Прибытие", buttonClickEvent -> {
                    Dialog dialog = new Dialog();
                    Shipment selectedShipment = grid.getSelectedItems().iterator().next();
                    ComboBox<PostOffice> postOfficeComboBox = new ComboBox<>("Место прибытия");
                    postOfficeComboBox.setItems(postOfficeRepo.findAll());

                    dialog.add(postOfficeComboBox, new Button("Сохранить", saveEvent -> {
                        selectedShipment.setStatus(StatusShipment.ПрибытиеВПромежуточноеОтделение);
                        selectedShipment.setCurrentPostOffice(postOfficeComboBox.getValue());
                        shipmentService.register(selectedShipment);

                        ShipmentHistory historyEntry = ShipmentHistory.builder()
                                .shipmentId(selectedShipment.getId())
                                .currentPostOffice(selectedShipment.getCurrentPostOffice())
                                .timestamp(LocalDateTime.now())
                                .status(selectedShipment.getStatus())
                                .build();
                        shipmentHistoryRepo.save(historyEntry);

                        dialog.close();
                        dialog.removeAll();
                        gridUpdate(shipmentService);
                    }));

                    dialog.open();
                }),
                new Button("Убытие", buttonClickEvent -> {
                    Dialog dialog = new Dialog();
                    Shipment selectedShipment = grid.getSelectedItems().iterator().next();

                    dialog.add(new Button("Сохранить", saveEvent -> {
                        selectedShipment.setStatus(StatusShipment.УбытиеИзПромежуточногоОтделения);
                        shipmentService.register(selectedShipment);

                        ShipmentHistory historyEntry = ShipmentHistory.builder()
                                .shipmentId(selectedShipment.getId())
                                .currentPostOffice(selectedShipment.getCurrentPostOffice())
                                .timestamp(LocalDateTime.now())
                                .status(selectedShipment.getStatus())
                                .build();
                        shipmentHistoryRepo.save(historyEntry);

                        dialog.close();
                        dialog.removeAll();
                        gridUpdate(shipmentService);
                    }));

                    dialog.open();
                }),
                new Button("Получение", buttonClickEvent -> {
                    Shipment selectedShipment = grid.getSelectedItems().iterator().next();
                    selectedShipment.setStatus(StatusShipment.Выдана);
                    selectedShipment.setCurrentPostOffice(selectedShipment.getDestinationPostOffice());
                    shipmentService.register(selectedShipment);

                    ShipmentHistory historyEntry = ShipmentHistory.builder()
                            .shipmentId(selectedShipment.getId())
                            .currentPostOffice(selectedShipment.getDestinationPostOffice())
                            .timestamp(LocalDateTime.now())
                            .status(selectedShipment.getStatus())
                            .build();
                    shipmentHistoryRepo.save(historyEntry);

                    gridUpdate(shipmentService);
                })
        )));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = getSave(shipmentService);
        Button delete = new Button("Удалить");
        delete.addClickListener(e -> {
            this.grid.getSelectedItems().forEach(shipmentService::delete);
            gridUpdate(shipmentService);
        });
        buttonLayout.add(save, delete);
        gridLayout.add(this.grid);
        add(gridLayout, buttonLayout);
        gridUpdate(shipmentService);
        this.postOfficeRepo = postOfficeRepo;
        this.shipmentHistoryRepo = shipmentHistoryRepo;
    }

    private Button getSave(ShipmentService shipmentService) {
        Button save = new Button("Регистрация");

        save.addClickListener(e -> {
            dialog = new Dialog();
            name = new TextField("Название посылки");
            type = new ComboBox<>("Тип посылки");
            type.setItems(TypeShipment.values());
            index = new TextField("Индекс получателя");
            address = new TextField("Адрес получателя");
            originPostOffice = new ComboBox<>("Место отправки");
            originPostOffice.setItems(postOfficeRepo.findAll());
            destinationPostOffice = new ComboBox<>("Место доставки");
            destinationPostOffice.setItems(postOfficeRepo.findAll());
            saveOnDialog = new Button("Сохранить", buttonClickEvent -> {
                Shipment shipment = Shipment.builder()
                        .name(name.getValue())
                        .type(type.getValue())
                        .index(index.getValue())
                        .address(address.getValue())
                        .originPostOffice(originPostOffice.getValue())
                        .currentPostOffice(originPostOffice.getValue())
                        .destinationPostOffice(destinationPostOffice.getValue())
                        .status(StatusShipment.Регистрация)
                        .build();
                shipmentService.register(shipment);
                shipmentHistoryRepo.save(ShipmentHistory.builder()
                        .shipmentId(shipment.getId())
                        .currentPostOffice(shipment.getCurrentPostOffice())
                        .timestamp(LocalDateTime.now())
                        .status(shipment.getStatus())
                        .build());
                dialog.close();
                dialog.removeAll();
                gridUpdate(shipmentService);
            });
            cancelOnDialog = new Button("Отменить", buttonClickEvent -> {
                dialog.close();
                dialog.removeAll();
            });
            dialogLayout = new VerticalLayout();
            dialogLayout.add(name, type, address, index, originPostOffice, destinationPostOffice, saveOnDialog, cancelOnDialog);
            dialog.add(dialogLayout);
            dialog.open();
        });
        return save;
    }

    private void gridUpdate(ShipmentService shipmentService) {
        grid.setItems(shipmentService.getShipments());
    }
}
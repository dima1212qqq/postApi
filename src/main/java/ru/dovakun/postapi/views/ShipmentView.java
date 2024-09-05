package ru.dovakun.postapi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.enums.TypeShipment;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.repo.PostOfficeRepo;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;
import ru.dovakun.postapi.service.ShipmentService;

import java.time.LocalDateTime;

@Route(value = "shipment", layout = MainLayout.class)
public class ShipmentView extends Div {

    private final PostOfficeRepo postOfficeRepo;
    private final ShipmentHistoryRepo shipmentHistoryRepo;
    private final ShipmentService shipmentService;
    private Grid<Shipment> grid;
    private Dialog dialog;
    private TextField name;
    private ComboBox<TypeShipment> type;
    private TextField index;
    private TextField address;
    private ComboBox<PostOffice> originPostOffice;
    private ComboBox<PostOffice> destinationPostOffice;
    private Button saveOnDialog;
    private Button cancelOnDialog;
    private VerticalLayout dialogLayout;

    @Autowired
    public ShipmentView(ShipmentService shipmentService, PostOfficeRepo postOfficeRepo, ShipmentHistoryRepo shipmentHistoryRepo) {
        this.shipmentService = shipmentService;
        this.postOfficeRepo = postOfficeRepo;
        this.shipmentHistoryRepo = shipmentHistoryRepo;

        HorizontalLayout gridLayout = new HorizontalLayout();
        this.grid = new Grid<>();
        setupGrid();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveButton = createSaveButton();
        Button deleteButton = createDeleteButton();

        buttonLayout.add(saveButton, deleteButton);
        gridLayout.add(this.grid);

        add(gridLayout, buttonLayout);
        gridUpdate();
    }

    private void setupGrid() {
        this.grid.addColumn(Shipment::getName).setHeader("Название посылки");
        this.grid.addColumn(Shipment::getType).setHeader("Тип посылки");
        this.grid.addColumn(Shipment::getAddress).setHeader("Адрес получателя");
        this.grid.addColumn(Shipment::getIndex).setHeader("Индекс получателя");
        this.grid.addColumn(Shipment::getStatus).setHeader("Статус посылки");
        this.grid.addColumn(Shipment::getOriginPostOffice).setHeader("Место отправки");
        this.grid.addColumn(Shipment::getCurrentPostOffice).setHeader("Место нахождения");
        this.grid.addColumn(Shipment::getDestinationPostOffice).setHeader("Место доставки");
        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        this.grid.setItemDetailsRenderer(
                new ComponentRenderer<>(shipment -> createActionButtonLayout(shipment))
        );
    }

    private HorizontalLayout createActionButtonLayout(Shipment shipment) {
        Button arrivalButton = new Button("Прибытие", event -> handleShipmentStatusUpdate(shipment, StatusShipment.ПрибытиеВПромежуточноеОтделение, true));
        Button departureButton = new Button("Убытие", event -> handleShipmentStatusUpdate(shipment, StatusShipment.УбытиеИзПромежуточногоОтделения, false));
        Button receivedButton = new Button("Получение", event -> handleShipmentStatusUpdate(shipment, StatusShipment.Выдана, false));

        return new HorizontalLayout(arrivalButton, departureButton, receivedButton);
    }

    private void handleShipmentStatusUpdate(Shipment shipment, StatusShipment newStatus, boolean needsPostOffice) {
        Dialog dialog = new Dialog();

        if (needsPostOffice) {
            ComboBox<PostOffice> postOfficeComboBox = new ComboBox<>("Место прибытия");
            postOfficeComboBox.setItems(postOfficeRepo.findAll());

            dialog.add(postOfficeComboBox, new Button("Сохранить", saveEvent -> {
                shipment.setCurrentPostOffice(postOfficeComboBox.getValue());
                updateShipment(shipment, newStatus);
                dialog.close();
                gridUpdate();
            }));
        } else {
            Button saveButton = new Button("Сохранить", saveEvent -> {
                updateShipment(shipment, newStatus);
                dialog.close();
                gridUpdate();
            });
            dialog.add(saveButton);
        }

        dialog.open();
    }

    private void updateShipment(Shipment shipment, StatusShipment newStatus) {
        shipment.setStatus(newStatus);
        shipmentService.register(shipment);

        shipmentHistoryRepo.save(
                ShipmentHistory.builder()
                        .shipmentId(shipment.getId())
                        .currentPostOffice(shipment.getCurrentPostOffice())
                        .timestamp(LocalDateTime.now())
                        .status(shipment.getStatus())
                        .build()
        );
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Регистрация");

        saveButton.addClickListener(e -> {
            dialog = new Dialog();
            setupDialogFields();
            setupDialogButtons();

            dialogLayout = new VerticalLayout();
            dialogLayout.add(name, type, address, index, originPostOffice, destinationPostOffice, saveOnDialog, cancelOnDialog);

            dialog.add(dialogLayout);
            dialog.open();
        });

        return saveButton;
    }

    private Button createDeleteButton() {
        Button deleteButton = new Button("Удалить");

        deleteButton.addClickListener(e -> {
            this.grid.getSelectedItems().forEach(shipmentService::delete);
            gridUpdate();
        });

        return deleteButton;
    }

    private void setupDialogFields() {
        name = new TextField("Название посылки");
        type = new ComboBox<>("Тип посылки");
        type.setItems(TypeShipment.values());
        index = new TextField("Индекс получателя");
        address = new TextField("Адрес получателя");

        originPostOffice = new ComboBox<>("Место отправки");
        originPostOffice.setItems(postOfficeRepo.findAll());

        destinationPostOffice = new ComboBox<>("Место доставки");
        destinationPostOffice.setItems(postOfficeRepo.findAll());
    }

    private void setupDialogButtons() {
        saveOnDialog = new Button("Сохранить", e -> {
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
            shipmentHistoryRepo.save(
                    ShipmentHistory.builder()
                            .shipmentId(shipment.getId())
                            .currentPostOffice(shipment.getCurrentPostOffice())
                            .timestamp(LocalDateTime.now())
                            .status(shipment.getStatus())
                            .build()
            );

            dialog.close();
            dialog.removeAll();
            gridUpdate();
        });

        cancelOnDialog = new Button("Отменить", e -> {
            dialog.close();
            dialog.removeAll();
        });
    }

    private void gridUpdate() {
        grid.setItems(shipmentService.getShipments());
    }
}
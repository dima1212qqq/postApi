package ru.dovakun.postapi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.repo.PostOfficeRepo;
import ru.dovakun.postapi.service.PostOfficeService;

import java.util.List;

@Route(value = "postoffice", layout = MainLayout.class)
public class PostOfficeView extends Div {

    private Dialog dialog = new Dialog();
    private IntegerField index;
    private TextField name;
    private TextField address;
    private Button saveOnDialog;
    private Button cancelOnDialog;
    private VerticalLayout dialogLayout;
    private Grid<PostOffice> grid;

    PostOfficeView(@Autowired PostOfficeService postOfficeService) {
        HorizontalLayout gridLayout = new HorizontalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = getSave(postOfficeService);
        Button delete = new Button("Удалить", buttonClickEvent -> {
            grid.getSelectedItems().stream().forEach(postOffice -> postOfficeService.delete(postOffice.getIndex()));
            gridUpdate(postOfficeService);
        });
        Button update = getUpdate(postOfficeService);
        buttonLayout.add(save, delete, update);
        grid = new Grid<>();
        grid.addColumn(PostOffice::getIndex).setHeader("Индекс");
        grid.addColumn(PostOffice::getName).setHeader("Название отделения");
        grid.addColumn(PostOffice::getAddress).setHeader("Адрес отделения");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridUpdate(postOfficeService);
        gridLayout.add(grid);
        add(gridLayout, buttonLayout);
    }
    private Button getUpdate(PostOfficeService postOfficeService) {
        Button update = new Button("Изменить");
        update.addClickListener(e -> {
            index = new IntegerField("Индекс");
            index.setValue(Integer.parseInt(grid.getSelectedItems().iterator().next().getIndex()));
            name = new TextField("Название отделения");
            name.setValue(grid.getSelectedItems().iterator().next().getName());
            address = new TextField("Адрес отделения");
            address.setValue(grid.getSelectedItems().iterator().next().getAddress());
            saveOrUpdate(postOfficeService);
        });
        return update;
    }
    private Button getSave(PostOfficeService postOfficeService) {
        Button save = new Button("Создать");
        save.addClickListener(e -> {
            index = new IntegerField("Индекс");
            name = new TextField("Название отделения");
            address = new TextField("Адрес отделения");
            saveOrUpdate(postOfficeService);
        });
        return save;
    }

    private void saveOrUpdate(PostOfficeService postOfficeService) {
        saveOnDialog = new Button("Сохранить", buttonClickEvent -> {
            postOfficeService.create(PostOffice.builder()
                    .index(index.getValue().toString())
                    .name(name.getValue())
                    .address(address.getValue()).
                    build());
            dialog.close();
            dialog.removeAll();
            gridUpdate(postOfficeService);
        });
        cancelOnDialog = new Button("Отмена", buttonClickEvent -> {
            dialog.close();
            dialog.removeAll();
        });
        dialogLayout = new VerticalLayout();
        dialogLayout.add(index, name, address, saveOnDialog, cancelOnDialog);
        dialog.add(
                dialogLayout
        );
        dialog.open();
    }

    private void gridUpdate(@Autowired PostOfficeService postOfficeService) {
        if (!postOfficeService.getPostOffices().isEmpty())
            grid.setItems(postOfficeService.getPostOffices());
    }
}

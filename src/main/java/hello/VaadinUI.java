package hello;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bipin on 4/26/16.
 */
@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private final CustomerRepository repo;
    private final CustomerEditor editor;
    private final Grid grid;
    private final TextField filter;
    private final Button addNewBtn;


    @Autowired
    public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New Customer", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {
//        setContent(new Button("Click me", e-> Notification.show("Hello Spring + Vaadin")));
//        setContent(grid);
//        TextField filter = new TextField();
//        filter.setInputPrompt("Filter by last name");
//        filter.addTextChangeListener(e -> listCustomer(e.getText()));
//        VerticalLayout mainLayout = new VerticalLayout(filter, grid);
        // Build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
        setContent(mainLayout);

        // Configure layouts and components
        actions.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        grid.setHeight(300, Unit.PIXELS);
        //grid.setColumns("id", "firstName", "lastName");

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(e -> listCustomer(e.getText()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addSelectionListener(e -> {
                if (e.getSelected().isEmpty()) {
                    editor.setVisible(false);
                } else {
                    editor.editCustomer((Customer) grid.getSelectedRow());
                }
        });

        // Instantiate adn edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
                editor.setVisible(false);
                listCustomer(filter.getValue());
        });

        // Initialize listing
        listCustomer(null);

    }

    private void listCustomer(String text) {
        Iterable<Customer> customerIterable = repo.findAll();
        List<Customer> customerList = new ArrayList<>();
        for(Customer customer : customerIterable) {
            customerList.add(customer);
        }

        if(StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(
                    new BeanItemContainer(Customer.class, customerList));
        } else {
            grid.setContainerDataSource(
                    new BeanItemContainer(Customer.class,
                            repo.findByLastNameStartsWithIgnoreCase(text)));

        }
    }
}

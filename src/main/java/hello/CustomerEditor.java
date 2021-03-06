package hello;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bipin on 4/26/16.
 */
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

    private final CustomerRepository repository;

    //  currently edited customer
    private Customer customer;

    // Fields to edit properties in Customer entity
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    // Action buttons
    Button save = new Button("Save", FontAwesome.SAVE);
    Button delete = new Button("Delete", FontAwesome.TRASH_O);
    Button cancel = new Button("Cancel");
    CssLayout actions = new CssLayout(save, cancel, delete);

    @Autowired
    public CustomerEditor(CustomerRepository repository) {
        this.repository = repository;

        addComponents(firstName, lastName, actions);

        // Configure and style components
        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> repository.save(customer));
        delete.addClickListener(e -> repository.delete(customer));
        cancel.addClickListener(e -> editCustomer(customer));
        setVisible(false);

    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editCustomer(Customer c) {
        final boolean persisted = c.getId() != null;
        if(persisted) {
            // find fresh entity for editing
            customer = repository.findOne(c.getId());
        } else {
            customer = c;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        BeanFieldGroup.bindFieldsUnbuffered(customer, this);

        setVisible(true);

        // A hack to ensure the whole form is visible
        save.focus();
        // Select all text in firstName field automatically
        firstName.selectAll();

    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete is clicked
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }
}

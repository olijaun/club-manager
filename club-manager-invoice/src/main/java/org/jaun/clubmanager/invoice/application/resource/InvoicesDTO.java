package org.jaun.clubmanager.invoice.application.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class InvoicesDTO implements Serializable {

    private Collection<InvoiceDTO> invoices = Collections.emptyList();

    public Collection<InvoiceDTO> getInvoices() {
        return invoices;
    }

    public void setInvoices(Collection<InvoiceDTO> invoices) {
        if (invoices != null) {
            this.invoices = invoices;
        }
    }
}

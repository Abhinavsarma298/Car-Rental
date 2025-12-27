package com.payment.PaymentService.Util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.payment.PaymentService.Entity.InvoiceEntity;

import java.io.ByteArrayOutputStream;

public class InvoicePdfGenerator {

    public static byte[] generateInvoicePdf(InvoiceEntity invoice) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Payment Invoice", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber(), textFont));
            document.add(new Paragraph("Payment ID: " + invoice.getPaymentId(), textFont));
            document.add(new Paragraph("Amount: " + invoice.getAmount(), textFont));
            document.add(new Paragraph("Currency: " + invoice.getCurrency(), textFont));
            document.add(new Paragraph("Date: " + invoice.getCreatedAt(), textFont));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF");
        }

        return outputStream.toByteArray();
    }
}

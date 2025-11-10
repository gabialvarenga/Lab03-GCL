package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta de uma transação")
public class TransactionResponseDTO {

    @Schema(description = "ID único da transação", example = "1")
    private Long id;

    @Schema(description = "Quantidade de moedas transferidas", example = "50")
    private Integer amount;

    @Schema(description = "Data e hora da transação", example = "2025-11-10T14:30:00")
    private LocalDateTime date;

    @Schema(description = "Tipo da transação", example = "SENT")
    private TransactionType type;

    @Schema(description = "Motivo/descrição da transação", example = "Participação em projeto de extensão")
    private String reason;

    @Schema(description = "ID do remetente (quem enviou)", example = "2")
    private Long senderId;

    @Schema(description = "Nome do remetente", example = "Prof. João Silva")
    private String senderName;

    @Schema(description = "ID do destinatário (quem recebeu)", example = "1")
    private Long receiverId;

    @Schema(description = "Nome do destinatário", example = "Maria Santos")
    private String receiverName;

    @Schema(description = "Data de criação do registro", example = "2025-11-10T14:30:00")
    private LocalDateTime createdAt;

    public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
        this.type = transaction.getType();
        this.reason = transaction.getReason();
        this.createdAt = transaction.getCreatedAt();

        // Sender (pode ser null em caso de crédito do sistema)
        if (transaction.getSender() != null) {
            this.senderId = transaction.getSender().getId();
            this.senderName = transaction.getSender().getName();
        }

        // Receiver (pode ser null em caso de resgate)
        if (transaction.getReceiver() != null) {
            this.receiverId = transaction.getReceiver().getId();
            this.receiverName = transaction.getReceiver().getName();
        }
    }
}

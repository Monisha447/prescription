package com.example.demo.entity;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Schema(name = "Prescription", description = "This is model class of prescription, it contains property and getter-setter methods")
public class Prescription {
@Id
   private int  appointment_id;

    private String medicines;

    private String dosage;

    private String instruction;

    private String created_by;

    private LocalDateTime created_date;

    private String modified_by;

    private LocalDateTime modified_date;

    public Prescription()
    {

    }
public Prescription(int appointment_id,
            @Size(max = 10, message = "CountryName cannot be more than 60 characters") @NotEmpty String medicines,
            @Size(min = 3, max = 3, message = "Alpha code must be of 3 character") @NotEmpty String dosage,
            @Size(min = 3, max = 3, message = "Alpha code must be of 3 character") @NotEmpty String instruction,
            String created_by, LocalDateTime created_date, String modified_by, LocalDateTime modified_date) {
        this.appointment_id = appointment_id;
        this.medicines = medicines;
        this.dosage = dosage;
        this.instruction = instruction;
        this.created_by = created_by;
        this.created_date = created_date;
        this.modified_by = modified_by;
        this.modified_date = modified_date;
        
    }

    public int getId() {
        return appointment_id;
    }

    public void setId(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

   public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }
    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    public String getCreatedBy() {
        return created_by;
    }

    public void setCreatedBy(String created_by) {
        this.created_by = created_by;
    }

    public LocalDateTime getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(LocalDateTime created_date) {
        this.created_date = created_date;
    }

    public String getModifiedBy() {
        return modified_by;
    }

    public void setModifiedBy(String modified_by) {
        this.modified_by = modified_by;
    }

    public LocalDateTime getModifiedDate() {
        return modified_date;
    }

    public void setModifiedDate(LocalDateTime modified_date) {
        this.modified_date = modified_date;
    }

    @Override
    public String toString() {
        return "Prescription [id=" + appointment_id + ", dosage=" + dosage + ", medicines=" + medicines + ", instruction=" + instruction +",createdBy=" + created_by + ", createdDate=" + created_date + ", modifiedBy=" + modified_by + ", modifiedDate=" + modified_date + "]";
    }
}

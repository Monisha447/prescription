package com.example.demo.controller;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

//import org.apache.el.stream.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Prescription;
import com.example.demo.repository.PrescriptionRepository;
import com.example.demo.response.ResponseBean;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;


@RestController

public class PrescriptionController {
    private static final Logger log = LogManager.getLogger(PrescriptionController.class);
    
    
    @Autowired
    public PrescriptionRepository presrepo;
    @Autowired
    public ResponseBean response;
    public final MessageSource messageSource;

    public PrescriptionController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping(path="/Prescription", produces="application/Json")
    public ResponseEntity<Object> getMedicines() {
    List<Prescription> prescriptionList = presrepo.findAll();
    response.setData(prescriptionList);

    return new ResponseEntity<Object>(response, HttpStatus.OK);
    }


    @GetMapping(path="/Prescription/{appointment_id}", produces="application/Json")
    public ResponseEntity<Object> getPrescriptionById(
    @PathVariable("appointment_id") int appointment_id) {
    Optional<Prescription> prescription = presrepo.findById(appointment_id);
    
    if (prescription.isPresent()) {
        response.setData(prescription.get());
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    } else {
        String descMessage = messageSource.getMessage("ERROR_DESC01", null, LocaleContextHolder.getLocale());
        String messageCode = messageSource.getMessage("ERROR_ID01", null, LocaleContextHolder.getLocale());
        response.setErrorCode(messageCode);
        response.setErrorDesc(descMessage);
        response.setData(null);
        return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
    }
    }
    
    @PostMapping(path = "/Prescription", consumes = "application/JSon", produces = "application/Json")
    @Operation(summary = "Adds prescription information")
    public ResponseEntity<Object> addPres(@RequestBody @Valid Prescription prescription,
           // @RequestHeader(name = "Accept-Language", required = false) Locale locale,
            @Param("created_by") String created_by) {

        log.info("Start of Post request by -- " + created_by);
        try {
            if (presrepo.findById(prescription.getId()).isPresent()) {

                String descMessage = messageSource.getMessage("ERROR_DESC02", null, LocaleContextHolder.getLocale());
                String messageCode = messageSource.getMessage("ERROR_ID02", null, LocaleContextHolder.getLocale());

                response.setErrorCode(messageCode);
                response.setErrorDesc(descMessage);
                response.setData(null);
                log.error(descMessage + " at --" + prescription.getId() + " appointment_id");
                log.info("End of Post request by --" + created_by);
                return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);

            } else {

                prescription.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                prescription.setCreatedBy(created_by);
                Prescription save = presrepo.save(prescription);

                response.setErrorCode(null);
                response.setErrorDesc(null);
                response.setData(save);
                log.debug("Created -- " + save);
                log.info("End of Post request by --" + created_by);

                return new ResponseEntity<Object>(response, HttpStatus.CREATED);

            }
        } catch (Exception e) {

            log.error(e.getMessage());
            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(path = "/Prescription/{appointment_id}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Updates Prescription information ")
    public ResponseEntity<Object> updatePrescription(@RequestBody @Valid Prescription prescription,
            @PathVariable("appointment_id") @Parameter(description = "The Id of the Country to update.") int appointment_id,
            //@RequestHeader(name = "Accept-Language", required = false) Locale locale,
            @Param("modified_by") String modified_by, @Param("created_by") String created_by) {

                log.info("Start of Put request");
                
        try {
            Optional<Prescription> optionalPrescription = presrepo.findById(appointment_id);
            if (optionalPrescription.isPresent()) {

                Prescription pstn = optionalPrescription.get();
                pstn.setModifiedBy(modified_by);
                pstn.setModifiedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                pstn.setId(appointment_id);
                pstn.setMedicines(prescription.getMedicines());
                pstn.setDosage(prescription.getDosage());
                pstn.setInstruction(prescription.getInstruction());
                presrepo.save(pstn);

                response.setErrorCode(null);
                response.setErrorDesc(null);
                response.setData(pstn);
                log.debug("Updated Country present at "+appointment_id+ " id by "+ modified_by);
                log.info("End of Put request");
                return new ResponseEntity<Object>(response, HttpStatus.OK);

            } else {

                prescription.setId(appointment_id);
                prescription.setCreatedBy(created_by);
                prescription.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                response.setErrorCode(null);
                response.setErrorDesc(null);
                response.setData(presrepo.save(prescription));

                log.debug("New country created at "+ appointment_id+ " id by "+ created_by);
                log.info("End of Put request");
                return new ResponseEntity<Object>(response, HttpStatus.OK);

            }
        } catch (Exception e) {

            String descMessage = messageSource.getMessage("ERROR_DESC01", null, LocaleContextHolder.getLocale());
            String messageCode = messageSource.getMessage("ERROR_ID01", null, LocaleContextHolder.getLocale());

            response.setErrorCode(messageCode);
            response.setErrorDesc(descMessage);
            log.error(descMessage);
            response.setData(null);
            log.error(e.getMessage());
            log.info("End of Put request");
            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

    }
@PatchMapping(path = "/Prescription/{appointment_id}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Updates appointment information partially")
    public ResponseEntity<Object> partialUpdatePrescription(@RequestBody @Valid Map<Object, Object> objectMap,
            @PathVariable("appointment_id") @Parameter(description = "The Id of the Country to Update.") int appointment_id,
            //@RequestHeader(name = "Accept-Language", required = false) Locale loc,
            @Param("modified_by") String modified_by) {

        log.info("Start of Patch request");
    
        Optional<Prescription> optionalPrescription = presrepo.findById(appointment_id);
        if (optionalPrescription.isPresent()) {

            Prescription pstn = optionalPrescription.get();
            pstn.setModifiedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            pstn.setModifiedBy(modified_by);

            objectMap.forEach((key, value) -> {

                Field field = ReflectionUtils.findRequiredField(Prescription.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, pstn, value);

            });

            response.setErrorCode(null);
            response.setErrorDesc(null);
            response.setData(presrepo.save(pstn));

            log.debug("Updated prescription present at "+appointment_id + " id by "+ modified_by);
            log.info("End of Patch request");
            return new ResponseEntity<Object>(response, HttpStatus.OK);

        } else {

            String messageCode = messageSource.getMessage("ERROR_ID01", null, LocaleContextHolder.getLocale());
            String descMessage = messageSource.getMessage("ERROR_DESC01", null, LocaleContextHolder.getLocale());
            response.setErrorCode(messageCode);
            response.setErrorDesc(descMessage +"-- "+appointment_id);
            log.error(descMessage);
            response.setData(null);
            log.info("End of Patch request");

            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }

    }


    @DeleteMapping(path = "/Prescription/{appointment_id}")
    @Operation(summary = "Deletes prescription by its Id")
    public ResponseEntity<Object> deletePrescription(
            @PathVariable("appointment_id") @Parameter(description = "The Id of the Prescription to delete.") int appointment_id,
            //@RequestHeader(name = "Accept-Language", required = false) Locale locale,
            @Param("modified-by") String modified_by) {

        log.info("Start of Delete request");
        if (presrepo.findById(appointment_id).isPresent()) {

   // Log detailed debug information about the delete operation
            log.debug("prescription present at "+ appointment_id + " id is deleted by " + modified_by);

            presrepo.deleteById(appointment_id);

// Clear previous errors and set the response data to "deleted"
            response.setErrorCode(null);
            response.setErrorDesc(null);
            response.setData("deleted");
            log.info("End of Delete request");
            return new ResponseEntity<Object>(response, HttpStatus.OK);

        } else {

            String descMessage = messageSource.getMessage("ERROR_DESC01", null, LocaleContextHolder.getLocale());
            String messageCode = messageSource.getMessage("ERROR_ID01", null, LocaleContextHolder.getLocale());
            response.setErrorCode(messageCode);
            response.setErrorDesc(descMessage);
            log.error(descMessage +"-- "+appointment_id);
            response.setData(null);
            log.info("End of Delete request");
            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }

    }

}

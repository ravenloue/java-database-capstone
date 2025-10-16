package com.project.back_end.controllers;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.PrescriptionRepository;
import com.project.back_end.repo.ReportRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.ReportService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;

@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
@SuppressWarnings("removal")
public abstract class BaseControllerTest {
    
    @MockBean
    protected Service service;
    
    @MockBean
    protected TokenService tokenService;
    
    @MockBean
    protected AdminRepository adminRepository;
    
    @MockBean
    protected DoctorRepository doctorRepository;
    
    @MockBean
    protected DoctorService doctorService;
    
    @MockBean
    protected PatientRepository patientRepository;
    
    @MockBean
    protected PatientService patientService;
    
    @MockBean
    protected AppointmentService appointmentService;
    
    @MockBean
    protected AppointmentRepository appointmentRepository;
    
    @MockBean
    protected PrescriptionService prescriptionService;
    
    @MockBean
    protected PrescriptionRepository prescriptionRepository;
    
    @MockBean
    protected ReportService reportService;
    
    @MockBean
    protected ReportRepository reportRepository;
}
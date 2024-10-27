package com.example.pe_prm.ui.home;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe_prm.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DialogManager {
    private final Context context;
    private final DialogCallback callback;

    public interface DialogCallback {
        void onStudentAdded(Student student);  // Changed from (String name, String major)

        void onMajorAdded(Major major);

        void onStudentUpdated(Student updatedStudent);

        void onDeleteStudent(Student deletedStudent);

        void onShowDeleteSuccessMessage();
    }

    public DialogManager(Context context, DialogCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void showUpdateStudentDialog(Student student, List<Major> majorList, String currentMajorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_student, null);

        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_name_update);
        TextInputEditText dateInput = dialogView.findViewById(R.id.edit_text_date_update);
        TextInputEditText emailInput = dialogView.findViewById(R.id.edit_text_email_update);
        TextInputEditText addressInput = dialogView.findViewById(R.id.edit_text_address_update);
        Spinner genderSpinner = dialogView.findViewById(R.id.spinner_gender_update);
        Spinner majorSpinner = dialogView.findViewById(R.id.spinner_major_update);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel_update);
        MaterialButton btnUpdate = dialogView.findViewById(R.id.btn_update);

        // Thiết lập dữ liệu hiện tại
        nameInput.setText(student.getName());
        dateInput.setText(student.getDate());
        emailInput.setText(student.getEmail());
        addressInput.setText(student.getAddress());

        // Setup Major Spinner
        ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                majorList.stream().map(Major::getNameMajor).collect(Collectors.toList())
        );
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);

        // Set current major selection
        int majorPosition = majorList.stream()
                .map(Major::getNameMajor)
                .collect(Collectors.toList())
                .indexOf(currentMajorName);
        if (majorPosition != -1) {
            majorSpinner.setSelection(majorPosition);
        }

        // Setup Gender Spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Male", "Female", "Other")
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set current gender selection
        int genderPosition = Arrays.asList("Male", "Female", "Other").indexOf(student.getGender());
        if (genderPosition != -1) {
            genderSpinner.setSelection(genderPosition);
        }

        AlertDialog dialog = builder.setView(dialogView).create();

        // Handle button clicks
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            if (validateInputs(nameInput, dateInput, emailInput, addressInput)) {
                Student updatedStudent = new Student(
                        student.getId(),
                        nameInput.getText().toString().trim(),
                        dateInput.getText().toString().trim(),
                        genderSpinner.getSelectedItem().toString(),
                        emailInput.getText().toString().trim(),
                        addressInput.getText().toString().trim(),
                        majorList.get(majorSpinner.getSelectedItemPosition()).getIdMajor()
                );
                callback.onStudentUpdated(updatedStudent);
                dialog.dismiss();

                // Show success message
                Toast.makeText(context, "Student updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Add animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    public void showDeleteConfirmationDialog(Student student) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_confirmation);

        // Make dialog background transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set dialog width to 90% of screen width
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        // Setup buttons
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        MaterialButton btnDelete = dialog.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            // Delete the student
            callback.onDeleteStudent(student);

            // Show success message using Toast instead of Snackbar
            callback.onShowDeleteSuccessMessage();

            dialog.dismiss();
        });

        dialog.show();
    }

    public void showAddStudentDialog(List<Major> majorList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_student, null);

        TextInputEditText idInput = dialogView.findViewById(R.id.edit_text_id);
        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_name);
        TextInputEditText dateInput = dialogView.findViewById(R.id.edit_text_date);
        TextInputEditText emailInput = dialogView.findViewById(R.id.edit_text_email);
        TextInputEditText addressInput = dialogView.findViewById(R.id.edit_text_address);
        Spinner genderSpinner = dialogView.findViewById(R.id.spinner_gender);
        Spinner majorSpinner = dialogView.findViewById(R.id.spinner_major);

        // Setup date picker
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput.setText(dateFormat.format(calendar.getTime()));
        };

        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(context, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup spinners (giữ nguyên như cũ)
        ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                majorList.stream().map(Major::getNameMajor).collect(Collectors.toList())
        );
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Male", "Female", "Other")
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        builder.setView(dialogView)
                .setTitle("Add New Student")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                // Validate all inputs
                if (validateAllInputs(idInput, nameInput, dateInput, emailInput, addressInput)) {
                    String majorId = majorList.get(majorSpinner.getSelectedItemPosition()).getIdMajor();
                    Student newStudent = new Student(
                            idInput.getText().toString().trim(),
                            nameInput.getText().toString().trim(),
                            dateInput.getText().toString().trim(),
                            genderSpinner.getSelectedItem().toString(),
                            emailInput.getText().toString().trim(),
                            addressInput.getText().toString().trim(),
                            majorId
                    );
                    callback.onStudentAdded(newStudent);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    private boolean validateAllInputs(TextInputEditText idInput, TextInputEditText nameInput,
                                      TextInputEditText dateInput, TextInputEditText emailInput,
                                      TextInputEditText addressInput) {
        boolean isValid = true;

        // Validate Student ID
        String studentId = idInput.getText().toString().trim();
        if (studentId.isEmpty()) {
            idInput.setError("Student ID is required");
            isValid = false;
        } else if (!studentId.matches("^(SS|SA|SI)\\d{4}$")) {
            idInput.setError("ID must start with SS, SA, or SI followed by 4 digits");
            isValid = false;
        } else {
            try {
                int number = Integer.parseInt(studentId.substring(2));
                if (number > 2000) {
                    idInput.setError("Number part cannot exceed 2000");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                idInput.setError("Invalid ID format");
                isValid = false;
            }
        }

        // Validate Name
        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Name is required");
            isValid = false;
        }

        // Validate Date
        if (dateInput.getText().toString().trim().isEmpty()) {
            dateInput.setError("Date of Birth is required");
            isValid = false;
        }

        // Validate Email
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            emailInput.setError("Invalid email format");
            isValid = false;
        }

        // Validate Address
        if (addressInput.getText().toString().trim().isEmpty()) {
            addressInput.setError("Address is required");
            isValid = false;
        }

        return isValid;
    }

    public void showStudentDetailsDialog(Student student, String majorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_detail_student, null);

        TextView idText = dialogView.findViewById(R.id.text_id);
        TextView nameText = dialogView.findViewById(R.id.text_name);
        TextView dateText = dialogView.findViewById(R.id.text_date);
        TextView genderText = dialogView.findViewById(R.id.text_gender);
        TextView emailText = dialogView.findViewById(R.id.text_email);
        TextView addressText = dialogView.findViewById(R.id.text_address);
        TextView majorText = dialogView.findViewById(R.id.text_major);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close);

        idText.setText(String.format("ID: %s", student.getId()));
        nameText.setText(String.format("Name: %s", student.getName()));
        dateText.setText(String.format("Date: %s", student.getDate()));
        genderText.setText(String.format("Gender: %s", student.getGender()));
        emailText.setText(String.format("Email: %s", student.getEmail()));
        addressText.setText(String.format("Address: %s", student.getAddress()));
        majorText.setText(String.format("Major: %s", majorName));

        AlertDialog dialog = builder.setView(dialogView).create();

        // Set window animations
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Handle close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }

    private boolean validateInputs(TextInputEditText... inputs) {
        boolean isValid = true;
        for (TextInputEditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("This field is required");
                isValid = false;
            }
        }
        return isValid;
    }

    private String generateStudentId() {
        // Choose a random prefix
        String[] prefixes = {"SE", "SS", "SA"};
        String prefix = prefixes[(int) (Math.random() * prefixes.length)];

        // Generate a random number between 0 and 2000
        int randomNumber = (int) (Math.random() * 2000);

        // Format the ID as prefix + random number (zero-padded to 4 digits)
        return prefix + String.format("%04d", randomNumber);
    }


    public void showAddMajorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_major, null);

        TextInputEditText idInput = dialogView.findViewById(R.id.edit_text_major_id);
        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_major_name);

        builder.setView(dialogView)
                .setTitle("Add New Major")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                // Validate inputs
                String majorId = idInput.getText().toString().trim();
                String majorName = nameInput.getText().toString().trim();

                if (majorId.isEmpty() || majorName.isEmpty()) {
                    // Show an error message if inputs are empty
                    Toast.makeText(context, "Both fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the new major
                callback.onMajorAdded(new Major(majorId, majorName));
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
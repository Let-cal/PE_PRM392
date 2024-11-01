package com.example.pe_prm.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.text.TextUtils;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DialogManager {
    private final Context context;
    private final DialogCallback callback;

    public interface DialogCallback {
        void onItemAdded(Item item);
        void onTypeAdded(Type type);
        void onItemUpdated(Item updatedItem);
        void onDeleteItem(Item deletedItem);
        void onShowDeleteSuccessMessage();
    }

    public DialogManager(Context context, DialogCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void showUpdateItemDialog(Item item, List<Type> typeList, String currentTypeName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_item, null);

        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_name_update);
        TextInputEditText creatorInput = dialogView.findViewById(R.id.edit_text_creator_update);
        TextInputEditText releaseDateInput = dialogView.findViewById(R.id.edit_text_release_date_update);
        TextInputEditText identifierInput = dialogView.findViewById(R.id.edit_text_identifier_update);
        Spinner typeSpinner = dialogView.findViewById(R.id.spinner_type_update);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel_update);
        MaterialButton btnUpdate = dialogView.findViewById(R.id.btn_update);

        // Set current data
        nameInput.setText(item.getName());
        creatorInput.setText(item.getCreator());
        releaseDateInput.setText(item.getReleaseDate());
        identifierInput.setText(item.getIdentifier());

        // Setup date picker
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            releaseDateInput.setText(dateFormat.format(calendar.getTime()));
        };

        // Show date picker when clicking on release date input
        releaseDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            // If there's an existing date, parse it and set the calendar
            try {
                if (!TextUtils.isEmpty(releaseDateInput.getText())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date date = dateFormat.parse(releaseDateInput.getText().toString());
                    if (date != null) {
                        calendar.setTime(date);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            new DatePickerDialog(context,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Disable keyboard for release date input
        releaseDateInput.setInputType(InputType.TYPE_NULL);
        releaseDateInput.setFocusable(false);

        // Rest of your existing code...
        // Setup Type Spinner
        List<String> typeDisplayList = typeList.stream()
                .map(type -> type.getNameType() + " (" + type.getIdType() + ")")
                .collect(Collectors.toList());

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                typeDisplayList
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Set current type selection
        int typePosition = typeList.stream()
                .map(Type::getIdType)
                .collect(Collectors.toList())
                .indexOf(item.getType());
        if (typePosition != -1) {
            typeSpinner.setSelection(typePosition);
        }

        AlertDialog dialog = builder.setView(dialogView).create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            if (validateInputs(nameInput, creatorInput, releaseDateInput, identifierInput)) {
                int selectedPosition = typeSpinner.getSelectedItemPosition();
                Type selectedType = typeList.get(selectedPosition);

                Item updatedItem = new Item(
                        item.getId(),
                        nameInput.getText().toString().trim(),
                        creatorInput.getText().toString().trim(),
                        releaseDateInput.getText().toString().trim(),
                        selectedType.getIdType(),
                        identifierInput.getText().toString().trim(),
                        selectedType.getIdType()
                );
                callback.onItemUpdated(updatedItem);
                dialog.dismiss();
                Toast.makeText(context, "Item updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    public void showDeleteConfirmationDialog(Item item) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_confirmation);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        MaterialButton btnDelete = dialog.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            callback.onDeleteItem(item);
            callback.onShowDeleteSuccessMessage();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showAddItemDialog(List<Type> typeList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);

        TextInputEditText idInput = dialogView.findViewById(R.id.edit_text_id);
        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_name);
        TextInputEditText creatorInput = dialogView.findViewById(R.id.edit_text_creator);
        TextInputEditText releaseDateInput = dialogView.findViewById(R.id.edit_text_release_date);
        TextInputEditText identifierInput = dialogView.findViewById(R.id.edit_text_identifier);
        Spinner typeSpinner = dialogView.findViewById(R.id.spinner_type);

        // Setup date picker
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            releaseDateInput.setText(dateFormat.format(calendar.getTime()));
        };

        releaseDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(context, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                typeList.stream().map(Type::getNameType).collect(Collectors.toList())
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        builder.setView(dialogView)
                .setTitle("Add New Item")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                if (validateAllInputs(idInput, nameInput, creatorInput, releaseDateInput, identifierInput)) {
                    String typeId = typeList.get(typeSpinner.getSelectedItemPosition()).getIdType();
                    Item newItem = new Item(
                            idInput.getText().toString().trim(),
                            nameInput.getText().toString().trim(),
                            creatorInput.getText().toString().trim(),
                            releaseDateInput.getText().toString().trim(),
                            typeId,
                            identifierInput.getText().toString().trim(),
                            typeId
                    );
                    callback.onItemAdded(newItem);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    public void showItemDetailsDialog(Item item, String typeName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_detail_item, null);

        TextView idText = dialogView.findViewById(R.id.text_id);
        TextView nameText = dialogView.findViewById(R.id.text_name);
        TextView creatorText = dialogView.findViewById(R.id.text_creator);
        TextView releaseDateText = dialogView.findViewById(R.id.text_release_date);
        TextView identifierText = dialogView.findViewById(R.id.text_identifier);
        TextView typeText = dialogView.findViewById(R.id.text_type);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close);

        idText.setText(String.format("ID: %s", item.getId()));
        nameText.setText(String.format("%s", item.getName()));
        creatorText.setText(String.format("Creator: %s", item.getCreator()));
        releaseDateText.setText(String.format("Release Date: %s", item.getReleaseDate()));
        identifierText.setText(String.format("Identifier: %s", item.getIdentifier()));
        typeText.setText(String.format("Type: %s", typeName));

        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private boolean validateAllInputs(TextInputEditText idInput, TextInputEditText nameInput,
                                      TextInputEditText creatorInput, TextInputEditText releaseDateInput,
                                      TextInputEditText identifierInput) {
        boolean isValid = true;

        if (idInput.getText().toString().trim().isEmpty()) {
            idInput.setError("Item ID is required");
            isValid = false;
        }

        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Name is required");
            isValid = false;
        }

        if (creatorInput.getText().toString().trim().isEmpty()) {
            creatorInput.setError("Creator is required");
            isValid = false;
        }

        if (releaseDateInput.getText().toString().trim().isEmpty()) {
            releaseDateInput.setError("Release date is required");
            isValid = false;
        }

        if (identifierInput.getText().toString().trim().isEmpty()) {
            identifierInput.setError("Identifier is required");
            isValid = false;
        }

        return isValid;
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

    public void showAddTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_type, null);

        TextInputEditText idInput = dialogView.findViewById(R.id.edit_text_type_id);
        TextInputEditText nameInput = dialogView.findViewById(R.id.edit_text_type_name);

        builder.setView(dialogView)
                .setTitle("Add New Type")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String typeId = idInput.getText().toString().trim();
                String typeName = nameInput.getText().toString().trim();

                if (typeId.isEmpty() || typeName.isEmpty()) {
                    Toast.makeText(context, "Both fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                callback.onTypeAdded(new Type(typeId, typeName));
                dialog.dismiss();
            });
        });
        dialog.show();
    }
}
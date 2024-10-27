package com.example.pe_prm.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pe_prm.R;
import com.example.pe_prm.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements StudentTableAdapter.OnItemClickListener , DialogManager.DialogCallback{
    private FloatingActionButton fabMain, fabAddStudent, fabAddMajor;
    private DialogManager dialogManager;
    private boolean isFabMenuOpen = false;
    private FragmentHomeBinding binding;
    private RecyclerView studentTable;
    private TabLayout tabLayout;
    private List<Student> allStudents;
    private StudentTableAdapter adapter;

    private void setupFab() {
        fabMain = binding.fabMain;
        fabAddStudent = binding.fabAddStudent;
        fabAddMajor = binding.fabAddMajor;

        fabMain.setOnClickListener(v -> toggleFabMenu());
        fabAddStudent.setOnClickListener(v -> {
            toggleFabMenu();
            dialogManager.showAddStudentDialog(majorList);
        });
        fabAddMajor.setOnClickListener(v -> {
            toggleFabMenu();
            dialogManager.showAddMajorDialog();
        });
    }

    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }
    private void openFabMenu() {
        isFabMenuOpen = true;
        fabMain.animate().rotation(45f).setDuration(300).start();
        fabAddStudent.show();
        fabAddMajor.show();

        fabAddStudent.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_65))
                .setDuration(300)
                .start();

        fabAddMajor.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_130))
                .setDuration(300)
                .start();
    }

    private void closeFabMenu() {
        isFabMenuOpen = false;
        fabMain.animate().rotation(0f).setDuration(300).start();

        fabAddStudent.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction(() -> fabAddStudent.hide())
                .start();

        fabAddMajor.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction(() -> fabAddMajor.hide())
                .start();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dialogManager = new DialogManager(requireContext(), this);
        setupFab();


        studentTable = binding.studentTable;
        tabLayout = binding.tabLayout;

        // Setup TabLayout with custom tabs
        setupTabLayout();

        // Setup RecyclerView
        studentTable.setLayoutManager(new LinearLayoutManager(getContext()));
        allStudents = generateDummyData(); // Method to generate dummy data
        adapter = new StudentTableAdapter(allStudents, this);
        studentTable.setAdapter(adapter);

        // Setup tab listener
        setupTabListener();

        return root;
    }

    @Override
    public void onStudentAdded(Student newStudent) {
        allStudents.add(0, newStudent);
        adapter.updateData(allStudents);
    }
    @Override
    public void onShowDeleteSuccessMessage() {
        Snackbar.make(binding.getRoot(), "Student deleted successfully", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.md_theme_error, null))
                .setTextColor(Color.WHITE)
                .show();
    }
    public void onDeleteStudent(Student student) {
        // Xóa sinh viên khỏi danh sách allStudents
        allStudents.remove(student);

        // Cập nhật adapter với danh sách mới
        adapter.updateData(new ArrayList<>(allStudents));
    }
    @Override
    public void onMajorAdded(Major major) {
        Major newMajor = new Major(major.getIdMajor(), major.getNameMajor()); // Use the correct property
        majorList.add(newMajor);
    }
    @Override
    public void onStudentUpdated(Student updatedStudent) {
        // Tìm và cập nhật student trong danh sách
        int index = -1;
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(updatedStudent.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            allStudents.set(index, updatedStudent);
            adapter.updateData(allStudents);
        }
    }

    private void setupTabLayout() {
        TabLayout.Tab allTab = tabLayout.newTab();
        allTab.setCustomView(createTabView("All", null)); // Replace with your icon
        tabLayout.addTab(allTab);

        TabLayout.Tab majorTab = tabLayout.newTab();
        majorTab.setCustomView(createTabView("By Major", R.drawable.baseline_arrow_drop_down_24));
        tabLayout.addTab(majorTab);
    }

    private View createTabView(String text, Integer iconResId) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
        TextView tabText = view.findViewById(R.id.tab_text);
        ImageView tabIcon = view.findViewById(R.id.tab_icon);

        tabText.setText(text);

        // Kiểm tra xem iconResId có phải là null không
        if (iconResId != null) {
            tabIcon.setImageResource(iconResId);
            tabIcon.setVisibility(View.VISIBLE); // Hiển thị icon nếu có
        } else {
            tabIcon.setVisibility(View.GONE); // Ẩn icon nếu không có
        }

        return view;
    }


    private List<String> getUniqueMajors() {
        return allStudents.stream()
                .map(Student::getMajor)
                .distinct()
                .collect(Collectors.toList());
    }

    private void showMajorSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Major");

        List<String> majors = getUniqueMajors();
        String[] majorArray = majors.toArray(new String[0]);

        builder.setItems(majorArray, (dialog, which) -> {
            String selectedMajor = majorArray[which];
            filterStudentData(selectedMajor);
        });

        builder.show();
    }

    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    filterStudentData(null); // Show all students
                } else {
                    showMajorSelectionDialog();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    showMajorSelectionDialog();
                }
            }
        });
    }

    private void filterStudentData(String major) {
        List<Student> filteredList;
        if (major == null) {
            filteredList = new ArrayList<>(allStudents);
        } else {
            filteredList = allStudents.stream()
                    .filter(student -> student.getMajor().equals(major))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filteredList);
    }

    @Override
    public void onItemClick(Student student, View itemView) {
        showPopupMenu(student, itemView);
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(Student student, View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.student_actions_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_update) {
                String majorName = majorList.stream()
                        .filter(m -> m.getIdMajor().equals(student.getIdMajor()))
                        .findFirst()
                        .map(Major::getNameMajor)
                        .orElse("Unknown");
                dialogManager.showUpdateStudentDialog(student, majorList, majorName);
                return true;
            } else if (itemId == R.id.action_delete) {
                dialogManager.showDeleteConfirmationDialog(student);
                return true;
            } else if (itemId == R.id.action_view_details) {
                String majorName = majorList.stream()
                        .filter(m -> m.getIdMajor().equals(student.getIdMajor()))
                        .findFirst()
                        .map(Major::getNameMajor)
                        .orElse("Unknown");
                dialogManager.showStudentDetailsDialog(student, majorName);
                return true;
            }
            return false;
        });
        popup.show();
    }



    private List<Major> majorList = new ArrayList<>(Arrays.asList(
            new Major("CS", "Computer Science"),
            new Major("BA", "Business Administration"),
            new Major("ENG", "Engineering"),
            new Major("IT", "Information Technology"),
            new Major("MKT", "Marketing")
    ));
    private List<Student> generateDummyData() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("SE1311", "John Doe", "CS"));
        students.add(new Student("SE1211", "Jane Smith", "BA"));
        students.add(new Student("SE1381", "Bob Johnson", "CS"));
        students.add(new Student("SE1399", "Alice Brown", "ENG"));
        students.add(new Student("SS1311", "Charlie Davis", "BA"));
        students.add(new Student("SE1412", "Eva Wilson", "ENG"));
        return students;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

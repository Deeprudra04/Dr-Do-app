# 📱 Dr. DO — Smart Task Management App

Dr. DO is an intelligent to-do and task management app designed to simplify your daily workflow through a clean UI, smart NLP-powered task parsing, and integrated content management.

---

## 🚀 Features

### 🏁 Entry Interface
Upon launching the app, users can choose between:

- **➕ Set Works** — to create a new task  
- **📋 Manage Works** — to view and manage all created tasks

---

## ✍️ Module: Set Works

### A. Manual Assignment
Users can manually create a task with:

- **Task Title**
- **Task Description** *(optional)*
- **Date Picker** (Calendar UI)
- **Time Picker** (Clock UI)
- **Reminder Options**:
  - 🔔 Alarm 10 minutes before the task
  - 🕓 Notification 1 day before the task

---

### B. Text Command-Based Assignment (Natural Language Input)
Supports smart text parsing for quick task entry.  
Example input:


Automatically parsed as:

- **Task Title**: *Join a meeting*  
- **Date**: *13 July*  
- **Time**: *12:00 PM*  
- **Reminders**: Both alarm and notification set automatically

> 🔍 **NLP Suggestion**: [Java Natty](https://natty.joestelmach.com/) used for date-time parsing.

---

### C. Attachments
Each task can include:

- 🎤 Voice recordings (recorded in-app)
- 📄 PDF, Word, and Excel files
- 🖼 Screenshots or images
- 🔗 Web links

All attachments are linked to the respective task and stored securely.

---

## 📁 Module: Manage Works

### A. Tabular Task Display
Tasks are listed in a structured table with the following columns:

| Task Name | Deadline (Date/Time) | Contents |
|-----------|----------------------|----------|

- Clicking on **Contents** opens all attached files and links related to that task.

### B. Task Management Options
Features above the table:

- ✏️ Edit task title, date, or time
- 📎 Add or remove attachments
- ❌ Delete tasks

> ⚠️ Edits automatically update the reminder and notification settings.

---

## 🔔 Notifications and Reminders

Each task supports:

- Daily notifications at the same time until the deadline
- Alarm reminders (10 minutes before)
- In-app notifications with interactive UI
- Notifications work even if the device is locked

---

## 🧠 Tech Stack

- **Language**: Kotlin
- **UI**: Android XML + Jetpack
- **Task Scheduling**: `WorkManager`, `AlarmManager`
- **NLP Parser**: Java Natty (for natural language input)
- **Storage**: Room DB / Internal Storage for files

---

## 🖼 App UI Screenshots

### 🔹 Home Screen
![Home Screen](UI_Scrreenshot/WhatsApp%20Image%202025-07-24%20at%2022.31.24_47b4e57b.jpg)

### 🔹 Set Works UI
![Set Work Screen](images/set_work_ui.png)

### 🔹 Manage Works UI
![Manage Work Screen](images/manage_work_ui.png)
> []*(UI_Scrreenshot/WhatsApp%20Image%202025-07-24%20at%2022.31.24_47b4e57b.jpg)*

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 🙌 Contributions

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📫 Contact

For queries or suggestions, feel free to reach out via [GitHub Issues](https://github.com/yourusername/your-repo-name/issues).


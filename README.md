# ExamsProject

Welcome to ExamsProject, an online examination platform designed to streamline the process of creating, administering, and evaluating exams in educational institutions. This comprehensive solution provides a user-friendly interface for teachers to create exams, allows students to take exams securely, and facilitates efficient grading and analysis of exam results. Built with Java and Maven, ExamsProject utilizes Undertow for web server capabilities and MySQL as the database management system.

## Technologies Used
- **Java**: Programming language used for the backend development.
- **Maven**: Build automation tool used for managing project dependencies and building the project.
- **Undertow**: Lightweight web server used for handling HTTP requests and serving web content.
- **MySQL**: Relational database management system used for storing exam-related data.

## Installation and Running Locally

**Requirements**

Ensure you have the following dependencies installed on your system:

- **Java**: `version 17.0.10`

### Installation and Running ExamsProject

1. Clone the ExamsProject repository:

    ```sh
    git clone https://github.com/Mwakisaghu/ExamsProject.git
    ```

2. Change to the project directory:

    ```sh
    cd ExamsProject
    ```

3. Install the dependencies:

    ```sh
    mvn clean install
    ```

4. Once the dependencies are installed successfully, start the application using Maven:

    ```sh
    mvn exec:java
    ```

## Running with IntelliJ

To run the project with **IntelliJ IDEA**, follow these steps:

1. **Open IntelliJ IDEA**.
2. Click on **"File"** -> **"Open"** and select the root directory of the ExamsProject.
3. Once the project is loaded, **navigate to the main class**.
4. **Right-click** on the main class and select **"Run"** or press the **"Run"** button in the toolbar.


## Contributing
Contributions are welcomed!
<details>
    <summary>Contributing Guidelines</summary>

    Fork the Repository: Start by forking the project repository to your GitHub account.

    Clone Locally: Clone the forked repository to your local machine using a Git client.

    ```sh
    git clone https://github.com/Mwakisaghu/ExamsProject
    ```

    Create a New Branch:

    ```sh
    git checkout -b new-feature-x
    ```

    Make Your Changes: Develop and test your changes locally.

    Commit Your Changes: Commit with a clear message describing your updates.

    ```sh
    git commit -m 'Implemented new feature x.'
    ```

    Push to GitHub: Push the changes to your forked repository.

    ```sh
    git push origin new-feature-x
    ```

    Submit a Pull Request: Create a PR against the original project repository. Clearly describe the changes and their motivations.

    Once the PR is reviewed and approved, merge it into the main branch.
</details>

// El archivo JS es el puente entre mi HTML (frontend) y mi API REST de Spring Boot (backend)

// Bloque 1: Inicialización y Selectores (Preparación)
// Este bloque asegura que el código solo se ejecute cuando el HTML esté listo 
// y obtiene las referencias a los elementos clave de la página.

// Esta primera linea asegura que el codigo JS solo se ejecute despues de que los elementos de HTML hayan sido cargados por el browser.
document.addEventListener('DOMContentLoaded', () => { 

    // La URL base de la API REST se almacena en una constante:
    const API_URL = '/api/contactos'; 

    // Ahora los selectores de elementos del DOM (HTML) se almacenan en constantes:
    const contactoForm = document.getElementById('contacto-form'); // referencia al formulario de crear/editar contacto
    const contactosTableBody = document.querySelector('#contactos-table tbody'); // referencia al tbody dentro de la tabla, donde insertaremos los contactos como filas
    const filtroForm = document.getElementById('filtro-form'); // otras referencias...
    const submitButton = document.getElementById('submit-button');
    const cancelButton = document.getElementById('cancel-edit-button');
    const noContactosMessage = document.getElementById('no-contactos');
    // Bandera para rastrear si estamos en modo edición, o sea si estamos editando o creando un contacto.
    let isEditing = false; 

// Bloque 2: Función de Lectura (cargarContactos)
// Se comunica con la API REST (backend) para obtener datos y mostrarlos en la tabla (frontend).
    
    // Función para cargar y mostrar los contactos en la tabla:
    async function cargarContactos(url = API_URL) {
        try {
         
            const response = await fetch(url); // Pedimos el GET a la API.
            if (!response.ok) {                // Chequeamos que la respuesta sea exitosa.
                throw new Error(`Error HTTP: ${response.status}`);
            }
            const contactos = await response.json(); // Parseamos la respuesta JSON.
            renderContactos(contactos);
        } catch(error) {
            console.error('Error al cargar los contactos:', error);
            renderContactos([]); // Muestra la tabla vacia si hay error.
        }
    }

// BLOQUE 3: Función de Renderizado (renderContactos)
// Funcion para dibujar la tabla de contactos al cargar página.

    function renderContactos(contactos) {
        contactosTableBody.innerHTML = ''; // Limpia el contenido previo de la tabla.
    
    if (contactos.length === 0) { // Si no hay contactos, muestra el msje. de no hay contactos y oculta la tabla.
        noContactosMessage.style.display = 'block'; 
        document.getElementById('contactos-table').style.display = 'none';
    } else { // Si si hay contactos.
        noContactosMessage.style.display = 'none';  // No muestra el msje.
        document.getElementById('contactos-table').style.display = 'table'; // Si muestra la table.
        
        contactos.forEach(contacto => {
            const row = contactosTableBody.insertRow(); // Para cada contacto crea una fila.
           
            // Crea el HTML de la fila inyectando los datos.
            row.innerHTML = `
            <td>${contacto.id}</td>
            <td>${contacto.nombre}</td>
            <td>${contacto.apellido}</td>
            <td>${contacto.telefono || ''}</td>
            <td>${contacto.email || ''}</td>
            <td>${contacto.domicilio || ''}</td>
            <td>
                <button class="edit-btn" data-id="${contacto.id}">Editar</button>
                <button class="delete-btn" data-id="${contacto.id}">Eliminar</button>
            </td>
            `;
        });

        document.querySelectorAll('.edit-btn').forEach(button => {
            button.addEventListener('click', handleEdit);
        });

        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', handleDelete);
        })  
    }
}


    // BLOQUE 4: Funcionalidad de Edición y Eliminación (handleEdit y handleDelete)

    // Rellena el formulario en modo Edición (obtiene el contacto por su ID - GET)
    async function handleEdit(e) { 
        const id = e.target.getAttribute('data-id'); // obtiene el id del contacto almacenado en el atributo data-id del boton

        try {
            // Pide los datos del contacto especifico a la API (ej: /api/contactos/5)
            const response = await fetch(`${API_URL}/${id}`);
            if (!response.ok) throw new Error('Contacto no encontrado');

            const contacto = await response.json();

            // a) Rellenamos el formulario con los datos recibidos:
            document.getElementById('contacto-id').value = contacto.id; // ¡Importante! Activa el modo PUT
            document.getElementById('nombre').value = contacto.nombre;
            document.getElementById('apellido').value = contacto.apellido;
            document.getElementById('telefono').value = contacto.telefono || '';
            document.getElementById('email').value = contacto.email;
            document.getElementById('domicilio').value = contacto.domicilio;
        
            // b) Configurar el formulario a modo edicion:
            isEditing = true;
            submitButton.textContent = 'Actualizar Contacto'; // cambia el texto del boton
            cancelButton.style.display = 'inline-block'; // muestra el boton de cancelar
            contactoForm.scrollIntoView({ behavior: 'smooth'}); // mueve la vista hasta el formulario para el usuario
       
        } catch (error) {
            console.error('Error al cargar el contacto para edición:', error);
            alert('No se pudo cargar el contacto para editar.');
        }  
    }

    // Elimina el contacto (DELETE):
    async function handleDelete(e) {
        const id = e.target.getAttribute('data-id'); // obtiene el id del boton

        // Pedimos confirmacion antes de borrar
        if (confirm(`¿Está seguro de que desea eliminar el contacto con ID ${id}?`)) {
            try {
                const response = await fetch(`${API_URL}/${id}`, {method: 'DELETE'});

                if (!response.ok) {
                    throw new Error(`Fallo al eliminar: ${response.status}`)
                }

                // Si tiene exito recarga la lista para que el contacto desaparezca:
                cargarContactos();
            
            } catch (error) {
                console.error('Error al eliminar el contacto:', error);
                alert('No se pudo eliminar el contacto');
            }
        }
    }


    // Ponemos una funcion auxiliar para limpiar el formulario y devolverlo a modo crear.
    function resetForm() {
        isEditing = false; // pasamos el modo edicion a falso.
        contactoForm.reset(); // metodo nativo (?) para limpiar todos los campos del formulario.
        document.getElementById('contacto-id').value = ''; // asegura que el ID oculto quede vacio (?)
        submitButton.textContent = 'Guardar Contacto'; // el boton vuelve a decir "Guardar"
        cancelButton.style.display = 'none'; // ocultamos el boton "Cancelar Edicion"
    }

    // BLOQUE 5: Funcionalidad de Crear y Actualizar (POST y PUT)

    contactoForm.addEventListener('submit', async(e) => {
        e.preventDefault();

        // 1. Recolectar datos del formulario.
        const id = document.getElementById('contacto-id').value; // obtiene un ID, vacio para crear, con valor para editar.

        const data = {
            id: id || null, // usa el id si hay, si no es null
            nombre: document.getElementById('nombre').value,
            apellido: document.getElementById('apellido').value,
            telefono: document.getElementById('telefono').value ? parseInt(document.getElementById('telefono').value) : null,
            email: document.getElementById('email').value,
            domicilio: document.getElementById('domicilio').value
        };

        // 2. Decidir el método HTTP: Si hay ID, es PUT. Si no, es POST.
        const  method = id  ? 'PUT' : 'POST';

        try {
            // 3. Enviar la peticion a la API.
            const response = await fetch(API_URL, {
                method: method,
                headers: {'Content-Type': 'application/json'}, // especifica que el cuerpo de la peticion es JSON
                body: JSON.stringify(data)} // convierte el objeto JS 'data' a una cadena JSON
            );

            if (!response.ok) {                  // si hay un error HTTP (ej: 400, 500), se lanza una excepción
                    const errorBody = await response.text();
                throw new Error(`Fallo al ${id ? 'actualizar' : 'crear'} contacto: ${response.status} - ${errorBody}`);
            }
            
            // 4. Si la petición fue exitosa:     
            resetForm(); // limpia el formulario, vuelve al modo crear
            cargarContactos(); // carga la lista con el nuevo / modificado contacto
        
        } catch(error) {
            console.error(`Error en la operacion ${method}:`, error);
            alert(`Error: ${error.message}`); // muestra el mensaje de error
        }
    });

    // Asigna la funcion resetForm al boton "Cancelar"
    cancelButton.addEventListener('click', resetForm);

    // BLOQUE 6: Buscar y filtrar

    filtroForm.addEventListener('submit', (e) => {
        e.preventDefault(); // evita que se recargue la pagina

        const params = new URLSearchParams();

        // Obtenemos los valores de los campos de filtro
        const filtro = document.getElementById('filtro').value.trim();
        const email = document.getElementById('filtro-email').value.trim();
        const telefono = document.getElementById('filtro-telefono').value.trim();
        const domicilio = document.getElementById('filtro-domicilio').value.trim();

        // Agregamos el paramtero al URL solo si tiene contenido (filtro opcional)
        if (filtro) params.append('filtro', filtro);
        if (email) params.append('email', email);
        if (telefono) params.append('telefono', telefono);
        if (domicilio) params.append('domicilio', domicilio);

        const searchURL =  `${API_URL}/buscar?${params.toString()}`;

        cargarContactos(searchURL);
    });

    document.getElementById('clear-filter-button').addEventListener('click', () => {
        filtroForm.reset();
        cargarContactos();
    })

    // BLOQUE: Inicialización
    // Ejecutamos la llamada que arranca la aplicación.
    cargarContactos(); 

})
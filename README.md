# PWB - 2017
## Estructura básica
En el módulo **ejb** se encuentra la mayor parte de la lógica de negocio y en el módulo **web** se encuentran los servicios rest.

### Módulo ejb

#### Modelos
Ubicación: `ejb > *.pwb.model`

Los modelos son básicamente los objetos y para ejb son los *entities* y por ende se utilizan los *annotations* relevantes tales como `@Entity`, `@ManyToOne`, `@Id`, `@GeneratedValue`, etc. Leer bien sobre el mapeo de las relaciones.

También se pueden utilizar decoradores de *hibernate* para las restricciones : `NotNull`, `Size`, `Pattern`, etc. Estas validaciones luego pueden ser comprobadas utilizando la clase `CustomValidator`.

**IMPORTANTE:** Debido a como se implementan las relaciones en el jpa, hay objetos que envuelven a otros objetos, esto complica a la hora de enviar/recibir objetos utilizando JSON. Ya que, por ejemplo dada la siguiente clase:

```
@Entity
@XmlRootElement
@Table(name = "Venta")
public class Venta implements Serializable {
    
    ...

    @Id
    @GeneratedValue    
	private Long id;
    
    @ManyToOne    
    private Cliente cliente;    

    ...

```

Para enviar un objeto por JSON tendríamos que hacerlo de la siguiente manera:

```
	{
		"id":1,
		"cliente":{
					"id":2
					...
				  }
		...
	}
```

Lo cual dificulta a la hora de realizar consultas a través del servicio REST. Algo más preferible sería:

```
	{
		"id":1,
		"clienteId":2
		...
	}
```

Por este motivo se aprovecha el uso de los *annotations* `@Transient` del JPA que permite definir campos en el objeto que no serán mapeados a la Base de Datos y por ende pueden ser utilizados en la comunicación a través de los objetos JSON y `@JsonIgnore` que permite ignorar ciertos campos a la hora de serializar/deserializar los objetos. La entidad anterior quedaría algo así:

```
@Entity
@XmlRootElement
@Table(name = "Venta")
public class Venta implements Serializable {
    
    ...

    @Id
    @GeneratedValue    
	private Long id;
    
	@JsonIgnore 					// Ignorado por Json
    @ManyToOne    
    private Cliente cliente;    

	@Transient
	private Long clienteId;			// Ignorado por JPA

    ...

```

#### Repositorios
Ubicación: `ejb > *.pwb.repository`

Implementan las sentencias PSQL/SQL sobre las entidades.

#### Beans
Ubicación: `ejb > *.pwb.bean`

Los *beans* permiten implementar la lógica de negocio. Se recomienda crear interfaces e implementarlas para aquellos beans que serán utilizados fuera del módulo *ejb* ya que serán las interfaces las que serán posteriormente utilizadas a través de `@Inject` o `@Ejb`.

Hay que tener en cuenta que los Beans pueden lanzar excepciones de negocio, por lo cual los métodos que lo hagan deben añadir `throws LaExcepcionQuePuedeLanzar` para que sean manejadas por los invocadores.

### Módulo ejb

Este módulo contiene los servicios rest. Aquí se crean las clases que se encargan de manejar las peticiones y se utilizan los *annotations* de Jax RS `@GET` `@POST`, etc. Se utilizan los *beans* del módulo *ejb* mediante `@Ejb` o `@Inject` y hay que considerar que si un *bean* lanza excepciones también hay que agregar el `throw` al método desde el cual se llama al bean y el *annotation* `@CatchExceptions` el cual se encarga de capturar las excepciones y generar un objeto JSON con el error.



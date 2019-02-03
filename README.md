# Saman
Saman is a lightweight library for data processing like conversion, persisting or synchronizing.

Albizia Saman, originally Samanea Saman and called 'árbol de la lluvia' ('The Rain Tree') by locals is a plant originated in middle and south america. Besides its characteristic to close its leaves when its raining, the Albizia Saman is also the plant that is suspected to be able to convert more CO2 into its structure than any other plant on the world, nearly twice as much as the second contestant that is bamboo.

## Anonymous data processing

The base principle of Saman is the same as in Spring's _**ConversionService**_: provide a generic entry point for every consumer in need for data processing, but hide the component that actually processes the data.

In Saman, this entry point is the _com.mantledillusion.data.saman.**ProcessingService**_ interface, with  _com.mantledillusion.data.saman.**DefaultProcessingService**_ being the reference implementation. 

The **_ProcessingService_** implementations are expected to hold a set of _com.mantledillusion.data.saman.ProcessingService.**Processor**_ implementations. The _**Processor**\<SourceType, TargetType>_ is a generic interface for processing data from _SourceType_ to _TargetType_.

The class _com.mantledillusion.data.saman.**ProcessorRegistry**_ is able to build a matrix of _SourceType_ &#8594; _TargetType_ mappings out of a set of **_Processors_**, from which it is then able to provide a matching _**Processor**_ implementation for every specific _SourceType/TargetType_ combination. With this feature, _**ProcessorRegistry**_ can be used by _**ProcessingService**_ implementations to locate the correct _**Processor**_ for a processing; **_DefaultProcessingService_** is implemented that way.

The package _com.mantledillusion.data.saman.interfaces_ contains several _**Processor**_ extending interfaces for the most common use cases.

Using the _**ProcessorRegistry**_ containing the matrix of _**Processor**_ implementations, The _**ProcessingService**_ is able to provide generic processing to consumers over its _process*()_ methods. The setup has several benefits over simply implementing a non-generic set of converters and calling them individually:
- One reference to the _**ProcessingService**_ is enough for every consumer instead of referencing and calling multiple components directly.
- The _**Processor**_ actually performing the processing is unknown for the consumer of the _**ProcessingService**_; as a result, code erosion like "historical growth" is prevented at its origin.
- _**Processor**_ implementations are reused automatically if the _**ProcessingService**_ is called for the same _SourceType/TargetType_ combination from different parts of the code.
- Processing code is standardized automatically as it has to conform to the _**Processor**_ interface (or one of its extensions).

## Processor Hopping

The regular data type for ***Processor***s are self implemented POJOs which, in most cases, contain sub objects that require processing to. For example, **_PojoA_** might contain a field of **_PojoB_**, which itself contains some primitive type fields like **int** or **char**.

In Saman, ***Processor***s are not only called with the source object to process, but also with the instance of _**ProcessingService**_ performing the processing. As a result, a Processor for _**PojoA**_ might process that type's primitive fields, but will call the _**ProcessingService**_ to find a Processor for the processing of _**PojoB**_.

This approach has multple benefits:
- "**Separation of Concerns**" is enforced inheritly for processing code.
- More code is reused, as ***Processor***s can be triggered by consumers and by other ***Processor***s as well.
- _**Processor**_ code becomes much cleaner.

## Process Contexting

The "**Separation of Concerns**" approach of Saman works great and has a lot of benefits, but it causes a _**Processor**_ not knowing the context in which it is triggered. In most cases this is not necessary or even favorable, as it preserves the code's simplicity and enhances re-usability.

But occasionally, there are situations in which it is necessary for a Processor to know the context it is operating in. Imagine a ProcessingService containing 2 ***Synchonizer***s for the types _**DatabaseEntityA**_ and _**DatabaseEntityB**_, where A contains a _**List**_ of B. The _**Processor**_ for B might be interested in the instance of A its instance of B belongs to; for example, the validation of B differs in relation to how A's fields are set. Because of "**Separation of Concerns**", if B does not contain its A instance, there is no way a _**Processor**_ could get that instance of A.

This is where process contexting steps in. The _**ProcessingDelegate**_ given to ***Processor***s is not only able to perform sub-instance processing; it also holds a map of objects that defines the context the processing takes place in.

So in the scenario of above, the _**Processor**_ of A could add the A instance to the context, so the _**Processor**_ of B is able to access it again.

Note that contexts are redefined on every hop between processors, so it is impossible for processors to access or overwrite context data of another processing chains.
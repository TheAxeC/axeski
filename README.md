# AxeSki

For my studies at the University I'm worked on a Visual programming IDE with another student,  my best friend, Matthijs Kaminski.

The purpose of the IDE is to create "black boxes" which can send events (signals packed with data) to eachother. We take the idea of using drag-able blocks in a visual IDE and expand on it.

- - - -
##Events:

The user can create new events. The events are signals that the user will send between different instances (see below). An event is identified by a unique name, it can also contain data (in the form of numbers, strings and booleans).

The system itself also has some default events. These are dedicated events for key presses, mouse clicks (and release) and the start event to signify a program has started running.

![picture alt](http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_2-1024x640.png "Event Creation")

- - - -
##Classes:

The user can create classes. A class is identified by a unique name. A class has a number of input events to which it can react. These input events are composed of user-created events and default (system) events.

A class has two top level constructs, <em>handlers</em> and <em>functions. </em>A function is a block of code that can be called. A function is allowed to have parameters and a return. A handler is a special function. A handler is designed to catch input events send to a class instance. It can't have user-defined parameters, it always has a single parameter, the event it catches.

Member variables can be added to a class. These are instantiated for each instance of that class. There is no concept of static member variables.

A  class can have costumes, these are visual representations of a class. An instance will show it's "costume" and can switch to other registered "costumes" from his class.

Rephrased, a class is a collection of functions and handlers that react to input events, which can have visual representations.

[caption id="attachment_87" align="aligncenter" width="640"]<a href="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_3.png"><img class="wp-image-87 size-large" src="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_3-1024x640.png" alt="Using handlers" width="640" height="400" /></a> Using handlers[/caption]

[caption id="attachment_88" align="aligncenter" width="640"]<a href="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_4.png"><img class="wp-image-88 size-large" src="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_4-1024x640.png" alt="Using functions" width="640" height="400" /></a> Using functions[/caption]

<hr />

<strong><em>Instances:</em></strong>

An instance is an instantiation of a certain class. In the WireFrame view, it shows the input and output events from his class. These events can be connected to events from another instance to "emit" the event.

In the Canvas view, the user can see the visual representation (costume) of the instance. When the program is running, the user can use this view to emit key-press events, or mouse-click/release events.

[caption id="attachment_90" align="alignnone" width="640"]<a href="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_7.png"><img class="size-large wp-image-90" src="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_7-1024x640.png" alt="Connecting instances" width="640" height="400" /></a> Connecting instances[/caption]

[caption id="attachment_89" align="alignnone" width="640"]<a href="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_6.png"><img class="size-large wp-image-89" src="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_6-1024x640.png" alt="Visual representation of instances" width="640" height="400" /></a> Visual representation of instances[/caption]

<hr />

<strong><em>Debugging:</em></strong>

The user has the ability to step through his code. In the class view, it will highlight the current executing block.

<hr />

<strong><em>Data editor:</em></strong>

If the user wants to, he can enable the data editor. The data editor will show the XML file that represents the program he is building. If this XML is edited, it will make changes to the visual views.

[caption id="attachment_92" align="alignnone" width="640"]<a href="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_9.png"><img class="size-large wp-image-92" src="http://axel.thefaceless.be/wp-content/uploads/2015/05/psopv_2014-2015_screenshot_9-1024x640.png" alt="Data editor" width="640" height="400" /></a> Data editor[/caption]

<hr />

The Virtual machine is completely separate from the actual  IDE. To implement another VM, a developer needs to supple a different compiler to "compile" the blocks from the visual representation to the format that is preferred.

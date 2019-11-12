package grphaqladapter.objecttracer.impl;

import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.objecttracer.ObjectTracer;
import grphaqladapter.objecttracer.ObjectTracerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardObjectTracerFactory implements ObjectTracerFactory {

    private AdaptedGraphQLSchema schema;
    private Map<Class , ObjectTracerImpl> tracers;

    @Override
    public void initialize(AdaptedGraphQLSchema schema) {
        this.schema = schema;
        tracers = initTracers();
    }

    private Map<Class , ObjectTracerImpl> initTracers()
    {
        Map<Class , ObjectTracerImpl> all = new ConcurrentHashMap<>();
        for(DiscoveredInputType inputType : schema.discoveredInputTypes())
        {
            boolean created = false;
            for(DiscoveredObjectType objectType:schema.discoveredObjectTypes())
            {
                if(objectType.asMappedClass().baseClass().equals(
                        inputType.asMappedClass().baseClass()
                )){
                    all.put(inputType.asMappedClass().baseClass() ,
                            new ObjectTracerImpl(inputType , objectType, factory));
                    created = true;
                    break;
                }
            }
            if(!created)
            {
                all.put(inputType.asMappedClass().baseClass() , new ObjectTracerImpl(inputType, factory));
            }
        }

        for(DiscoveredObjectType objectType : schema.discoveredObjectTypes())
        {
            if(all.get(objectType.asMappedClass().baseClass())==null)
            {
                all.put(objectType.asMappedClass().baseClass() ,
                        new ObjectTracerImpl(objectType, factory));
            }
        }

        return all;
    }

    @Override
    public ObjectTracer getTracer(Class cls) {
        ObjectTracer tracer = tracers.get(cls);
        if(tracer==null)
        {
            for(Class clazz : tracers.keySet())
            {
                if(clazz.isAssignableFrom(cls))
                {
                    tracers.put(cls , tracers.get(clazz));
                    tracer = tracers.get(clazz);
                    break;
                }
            }
        }

        return tracer;
    }
}

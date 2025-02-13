package org.jboss.resteasy.reactive.common.processor.scanning;

import java.util.HashSet;
import java.util.Set;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.resteasy.reactive.common.processor.ResteasyReactiveDotNames;

public class ResteasyReactiveParameterContainerScanner {
    public static Set<DotName> scanParameterContainers(IndexView index, ApplicationScanningResult result) {
        Set<DotName> res = new HashSet<DotName>();
        for (DotName fieldAnnotation : ResteasyReactiveDotNames.JAX_RS_ANNOTATIONS_FOR_FIELDS) {
            for (AnnotationInstance annotationInstance : index.getAnnotations(fieldAnnotation)) {
                // FIXME: this only supports fields, not properties, but not sure beanparam supports them anyway
                if (annotationInstance.target().kind() == Kind.FIELD) {
                    ClassInfo klass = annotationInstance.target().asField().declaringClass();
                    if (result.keepClass(klass.name().toString())) {
                        res.add(klass.name());
                    }
                }
            }
        }
        return res;
    }
}

package com.vaadin.flow.spring.test;

import com.vaadin.flow.component.dependency.NpmPackage;

/**
 * Since @NpmPackage annotations should be discovered without a @Route
 * annotation, they have been separated here for the scanner to find (and avoid
 * being found some other way)
 *
 * @see com.vaadin.flow.spring.test.DoubleNpmAnnotationView
 */
@NpmPackage(value = "@polymer/paper-input", version = "3.2.1")
@NpmPackage(value = "@polymer/paper-checkbox", version = "3.1.0")
public class DoubleNpmAnnotationHolder {
}

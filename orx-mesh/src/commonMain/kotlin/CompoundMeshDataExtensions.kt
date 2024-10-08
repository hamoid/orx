package org.openrndr.extra.mesh

import org.openrndr.draw.VertexBuffer
import org.openrndr.draw.vertexBuffer

/**
 * Write compound mesh data to [VertexBuffer]
 */
fun ICompoundMeshData.toVertexBuffer(): VertexBuffer {
    val triangulated = this.triangulate()

    val triangleCount = triangulated.compounds.values.sumOf { it.polygons.size }

    val vertexBuffer = vertexBuffer(objVertexFormat, triangleCount * 3)

    var elementOffset = 0
    for (compound in compounds) {
        compound.value.toVertexBuffer(elementOffset, vertexBuffer)
        elementOffset += compound.value.polygons.size * 3
    }

    return vertexBuffer
}

/**
 * Convert compound mesh data to [IPolygon] compounds
 */
fun ICompoundMeshData.toPolygons(): Map<String, List<IPolygon>> {
    return compounds.mapValues { it.value.toPolygons() }
}
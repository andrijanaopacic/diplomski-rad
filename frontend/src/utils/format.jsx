export function formatDatum(isoString) {
  if (!isoString) return ''

  const datum = new Date(isoString)
  if (isNaN(datum.getTime())) return isoString 

  return datum.toLocaleString('sr-RS', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function formatSamoDatum(isoString) {
  if (!isoString) return ''

  const datum = new Date(isoString)
  if (isNaN(datum.getTime())) return isoString

  return datum.toLocaleDateString('sr-RS', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}